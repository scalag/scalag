package com.unihogsoft.scalag.compiler

import java.nio.ByteBuffer

import shapeless.HList
import com.unihogsoft.scalag.dsl.DSL._
import org.lwjgl.BufferUtils
import Opcodes.{Instruction, _}
import com.unihogsoft.scalag.compiler.Digest.DigestedExpression

import scala.reflect.runtime.universe._
import scala.runtime.IntRef
import scala.util.hashing.MurmurHash3
class DSLCompiler {

  val localSizeX = 128
  val localSizeY = 1
  val localSizeZ = 1

  val BOUND_VARIABLE = "bound"
  val GLSL_EXT_NAME = "GLSL.std.450"
  val GLSL_EXT_REF = 1
  val TYPE_VOID_REF = 2
  val VOID_FUNC_TYPE_REF = 3
  val MAIN_FUNC_REF = 4
  val GL_GLOBAL_INVOCATION_ID_REF = 5
  val GL_WORKGROUP_SIZE_REF = 6
  val HEADER_REFS_TOP = 7

  val scalarTypeDefInsn = Map[Type, Instruction](
    typeOf[Int32] -> Instruction(Op.OpTypeInt, List(WordVariable("result"), IntWord(32), IntWord(1))),
    typeOf[Float32] -> Instruction(Op.OpTypeFloat, List(WordVariable("result"), IntWord(32), IntWord(0)))
  )

  val typeStride = Map[Type, Int](
    typeOf[Int32] -> 4,
    typeOf[Float32] -> 4
  )

  def headers(): List[Words] = {
    Word(Array(0x03, 0x02, 0x23, 0x07)) :: // SPIR-V
    Word(Array(0x00, 0x00, 0x01, 0x00)) :: // Version: 1.0
    Word(Array(0x00, 0x00, 0x00, 0x00)) :: // Generator: null
    WordVariable(BOUND_VARIABLE) :: // Bound: To be calculated
    Word(Array(0x00, 0x00, 0x00, 0x00)) :: // Schema: 0
    Instruction(Op.OpCapability, List(Capability.Shader)) :: // OpCapability Shader
    Instruction(Op.OpExtInstImport, List(ResultRef(GLSL_EXT_REF), Text(GLSL_EXT_NAME))) :: // OpExtInstImport "GLSL.std.450"
    Instruction(Op.OpMemoryModel, List(AddressingModel.Logical, MemoryModel.GLSL450)) :: // OpMemoryModel Logical GLSL450
    Instruction(Op.OpEntryPoint, List(
      ExecutionModel.GLCompute, ResultRef(MAIN_FUNC_REF), Text("main"), ResultRef(GL_GLOBAL_INVOCATION_ID_REF)
    )) :: // OpEntryPoint GLCompute %MAIN_FUNC_REF "main" %GL_GLOBAL_INVOCATION_ID_REF
    Instruction(Op.OpExecutionMode, List(
      ResultRef(MAIN_FUNC_REF), ExecutionMode.LocalSize, IntWord(128), IntWord(1), IntWord(1)
    )) :: // OpExecutionMode %4 LocalSize 128 1 1
    Instruction(Op.OpSource, List(SourceLanguage.GLSL, IntWord(450))) :: // OpSource GLSL 450
    Instruction(Op.OpDecorate, List(
      ResultRef(GL_GLOBAL_INVOCATION_ID_REF), Decoration.BuiltIn, BuiltIn.GlobalInvocationId
    )) :: // OpDecorate %GL_GLOBAL_INVOCATION_ID_REF BuiltIn GlobalInvocationId
    Instruction(Op.OpDecorate, List(
      ResultRef(GL_WORKGROUP_SIZE_REF), Decoration.BuiltIn, BuiltIn.GlobalInvocationId
    )) ::
    Nil
  }

  case class ArrayBufferBlock(
    structTypeRef: Int, // %BufferX
    blockVarRef: Int, // %__X
    blockPointerRef: Int, // _ptr_Uniform_OutputBufferX
    memberArrayTypeRef: Int, // %_runtimearr_float_X
    binding: Int
  )

  case class Context(
    scalarTypeMap: Map[Type, Int] = Map(),
    funPointerTypeMap: Map[Int, Int] = Map(),
    uniformPointerMap: Map[Int, Int] = Map(),
    inputPointerMap: Map[Int, Int] = Map(),
    vectorTypeMap: Map[(Type, Int), Int] = Map(),
    funVecPointerTypeMap: Map[Int, Int] = Map(),
    uniformVecPointerMap: Map[Int, Int] = Map(),
    inputVecPointerMap: Map[Int, Int] = Map(),
    voidTypeRef: Int = -1,
    voidFuncTypeRef: Int = -1,

    constRefs: Map[(Type, Any), Int] = Map(),
    exprRefs: Map[String, Int] = Map(),
    inBufferBlocks: List[ArrayBufferBlock] = List(),
    outBufferBlocks: List[ArrayBufferBlock] = List(),
    nextResultId: Int = HEADER_REFS_TOP,
    nextBinding: Int = 0,
  )

  def initialContext: Context  = Context()

  def defineTypes(types: List[Type]): (List[Words], Context) = {
    val basicTypes = List(typeOf[Int32])
    (basicTypes ::: types).distinct.foldLeft((List[Words](), initialContext)) {
      case ((words, ctx), valType) =>
        val typeDefIndex = ctx.nextResultId
        val code = List(
          scalarTypeDefInsn(valType).replaceVar("result", typeDefIndex),
          Instruction(Op.OpTypePointer, List(ResultRef(typeDefIndex + 1), StorageClass.Function, IntWord(typeDefIndex))),
          Instruction(Op.OpTypePointer, List(ResultRef(typeDefIndex + 2), StorageClass.Uniform, IntWord(typeDefIndex))),
          Instruction(Op.OpTypePointer, List(ResultRef(typeDefIndex + 3), StorageClass.Input, IntWord(typeDefIndex))),
          Instruction(Op.OpTypeVector, List(ResultRef(typeDefIndex + 4), ResultRef(typeDefIndex), IntWord(3))),
          Instruction(Op.OpTypePointer, List(ResultRef(typeDefIndex + 5), StorageClass.Function, IntWord(typeDefIndex + 4))),
          Instruction(Op.OpTypePointer, List(ResultRef(typeDefIndex + 6), StorageClass.Uniform, IntWord(typeDefIndex + 4))),
          Instruction(Op.OpTypePointer, List(ResultRef(typeDefIndex + 7), StorageClass.Input, IntWord(typeDefIndex + 4))),
        )
        (code ::: words,
          ctx.copy(
            scalarTypeMap = ctx.scalarTypeMap + (valType -> typeDefIndex),
            funPointerTypeMap = ctx.funPointerTypeMap + (typeDefIndex -> (typeDefIndex + 1)),
            uniformPointerMap = ctx.uniformPointerMap + (typeDefIndex -> (typeDefIndex + 2)),
            inputPointerMap = ctx.inputPointerMap + (typeDefIndex -> (typeDefIndex + 3)),
            vectorTypeMap = ctx.vectorTypeMap + ((valType, 3) -> (typeDefIndex + 4)),
            funVecPointerTypeMap = ctx.funVecPointerTypeMap + (typeDefIndex -> (typeDefIndex + 5)),
            uniformVecPointerMap = ctx.uniformVecPointerMap + (typeDefIndex -> (typeDefIndex + 6)),
            inputVecPointerMap = ctx.inputVecPointerMap + (typeDefIndex -> (typeDefIndex + 7)),
            nextResultId = ctx.nextResultId + 8
          )
        )
    }
  }

  def createAndInitBlocks(blocks: List[Type], in: Boolean, context: Context): (List[Words], List[Words], Context) = {
    val (decoration, definition, newContext) = blocks.foldLeft((List[Words](), List[Words](), context))({ case ((decAcc, insnAcc, ctx), tpe) =>
      val block = ArrayBufferBlock(
        ctx.nextResultId,
        ctx.nextResultId + 1,
        ctx.nextResultId + 2,
        ctx.nextResultId + 3,
        ctx.nextBinding
      )

      val decorationInstructions = List[Words](
        Instruction(Op.OpDecorate, List(
          ResultRef(block.memberArrayTypeRef),
          Decoration.ArrayStride,
          IntWord(typeStride(tpe))
        )), // OpDecorate %_runtimearr_X ArrayStride [typeStride(type)]
        Instruction(Op.OpMemberDecorate, List(
          ResultRef(block.structTypeRef),
          IntWord(0),
          Decoration.Offset,
          IntWord(0)
        )), // OpMemberDecorate %BufferX 0 Offset 0
        Instruction(Op.OpDecorate, List(
          ResultRef(block.structTypeRef),
          Decoration.BufferBlock
        )), // OpDecorate %BufferX BufferBlock
        Instruction(Op.OpDecorate, List(
          ResultRef(block.blockVarRef),
          Decoration.DescriptorSet,
          IntWord(0)
        )), // OpDecorate %_X DescriptorSet 0
        Instruction(Op.OpDecorate, List(
          ResultRef(block.blockVarRef),
          Decoration.Binding,
          IntWord(block.binding)
        )), // OpDecorate %_X Binding [binding]
      )

      val definitionInstructions = List[Words](
        Instruction(Op.OpTypeRuntimeArray, List(
          ResultRef(block.memberArrayTypeRef),
          IntWord(context.scalarTypeMap(tpe))
        )), // %_runtimearr_X = OpTypeRuntimeArray %[typeOf(tpe)]
        Instruction(Op.OpTypeStruct, List(
          ResultRef(block.structTypeRef),
          IntWord(block.memberArrayTypeRef)
        )), // %BufferX = OpTypeStruct %_runtimearr_X
        Instruction(Op.OpTypePointer, List(
          ResultRef(block.blockPointerRef),
          StorageClass.Uniform,
          ResultRef(block.structTypeRef)
        )), // %_ptr_Uniform_BufferX= OpTypePointer Uniform %BufferX
        Instruction(Op.OpVariable, List(
          ResultRef(block.blockVarRef),
          ResultRef(block.blockPointerRef),
          StorageClass.Uniform,
        )), // %_X = OpVariable %_ptr_Uniform_X Uniform
      )

      val contextWithBlock = if(in) ctx.copy(
        inBufferBlocks = block :: ctx.inBufferBlocks
      ) else ctx.copy(
        outBufferBlocks = block :: ctx.outBufferBlocks
      )
      (decAcc ::: decorationInstructions, insnAcc ::: definitionInstructions, contextWithBlock.copy(
        nextResultId = contextWithBlock.nextResultId + 5,
        nextBinding =  contextWithBlock.nextBinding + 1
      ))
    })
    (decoration, definition, newContext)
  }

  def defineVoids(context: Context): (List[Words], Context) = {
    val voidDef = List[Words](
      Instruction(Op.OpTypeVoid, List(ResultRef(context.nextResultId))),
      Instruction(Op.OpTypeFunction, List(ResultRef(context.nextResultId + 1), ResultRef(context.nextResultId)))
    )
    val ctxWithVoid = context.copy(
      voidTypeRef = context.nextResultId,
      voidFuncTypeRef = context.nextResultId + 1
    )
    (voidDef, ctxWithVoid)
  }

  def initAndDecorateUniforms(ins: List[Type], outs: List[Type], context: Context): (List[Words], List[Words], Context)  = {
    val (inDecor, inDef, inCtx) = createAndInitBlocks(ins, in = true, context)
    val (outDecor, outDef, outCtx) = createAndInitBlocks(outs, in = false, inCtx)
    val (voidsDef, voidCtx) = defineVoids(outCtx)
    (inDecor ::: outDecor, voidsDef ::: inDef ::: outDef, voidCtx)
  }

  def createInvocationId(context: Context): (List[Words], Context) = {
    val definitionInstructions = List(
      Instruction(Op.OpConstant, List(ResultRef(context.nextResultId + 0), IntWord(localSizeX))),
      Instruction(Op.OpConstant, List(ResultRef(context.nextResultId + 1), IntWord(localSizeY))),
      Instruction(Op.OpConstant, List(ResultRef(context.nextResultId + 2), IntWord(localSizeZ))),
      Instruction(Op.OpTypeVector, List(
        ResultRef(context.nextResultId + 3),
        ResultRef(context.scalarTypeMap(typeOf[Int32])),
        IntWord(3)
      )),
      Instruction(Op.OpConstantComposite, List(
        ResultRef(GL_WORKGROUP_SIZE_REF),
        ResultRef(context.vectorTypeMap((typeOf[Int32], 3))),
      )),
    )
    (definitionInstructions, context)
  }

  def toWord(tpe: Type, value: Any): Words = tpe match {
    case t if t == typeOf[Int32] =>
      Word(intToBytes(value.asInstanceOf[Int]).toArray)
    case t if t == typeOf[Float32] =>
      val ib: Int = value match {
        case fl: Float => java.lang.Float.floatToIntBits(fl)
        case il: Int => il
      }
      Word(intToBytes(ib).toArray)
  }

  def defineConstants(exprs: List[DigestedExpression], ctx: Context): (List[Words], Context) = {
    val consts = exprs.collect {
      case DigestedExpression(_, c@Const(x), _) =>
        (c.valTypeTag.tpe.dealias, x)
    } :+ ((typeOf[Int32], 0))
    consts.foldLeft((List[Words](), ctx)) {
      case ((instructions, context), const) =>
        val insn = Instruction(Op.OpConstant, List(
          ResultRef(context.nextResultId),
          IntWord(context.scalarTypeMap(const._1)),
          (toWord _).tupled(const)
        ))
        val ctx = context.copy(
          constRefs = context.constRefs + (const -> context.nextResultId),
          nextResultId = context.nextResultId + 1
        )
        (instructions :+ insn, ctx)
    }
  }

  def compileTree(sortedTree: List[DigestedExpression], ctx: Context): (List[Words], Context) = {
    def compileExpressions(exprs: List[DigestedExpression], ctx: Context, acc: List[Words]): (List[Words], Context) = {
      if(exprs.isEmpty) (acc, ctx)
      else {
        val expr = exprs.head
        val (instructions, updatedCtx) = expr.expr match {
          case c@Const(x) =>
            val constRef = ctx.constRefs((c.valTypeTag.tpe.dealias, x))
            val updatedContext = ctx.copy(
              exprRefs = ctx.exprRefs + (expr.digest -> constRef)
            )
            (List(), updatedContext)
          case d@Dynamic("worker_index") =>
            val instructions = List(
              Instruction(Op.OpAccessChain, List(
                ResultRef(ctx.nextResultId),
                IntWord(ctx.inputPointerMap(ctx.scalarTypeMap(d.valTypeTag.tpe.dealias))),
                IntWord(GL_GLOBAL_INVOCATION_ID_REF),
                IntWord(0)
              )),
              Instruction(Op.OpLoad, List(
                ResultRef(ctx.nextResultId + 1),
                IntWord(ctx.scalarTypeMap(d.valTypeTag.tpe.dealias)),
                ResultRef(ctx.nextResultId)
              ))
            )
            val updatedContext = ctx.copy(
              exprRefs = ctx.exprRefs + (expr.digest -> (ctx.nextResultId + 1)),
              nextResultId = ctx.nextResultId + 2
            )
            (instructions, updatedContext)
          case diff@Diff(a, b) =>
            val addOpcode = a.valTypeTag.tpe.dealias match {
              case f if f <:< typeOf[FloatType] =>
                Op.OpFSub
              case i if i <:< typeOf[IntegerType] =>
                Op.OpISub
            }
            val instructions = List(
              Instruction(addOpcode, List(
                ResultRef(ctx.nextResultId),
                ResultRef(ctx.exprRefs(expr.dependencies(0).digest)),
                ResultRef(ctx.exprRefs(expr.dependencies(1).digest))
              ))
            )
            val updatedContext = ctx.copy(
              exprRefs = ctx.exprRefs + (expr.digest -> ctx.nextResultId),
              nextResultId = ctx.nextResultId + 1
            )
            (instructions, updatedContext)
          case sum@Sum(a, b) =>
            val addOpcode = a.valTypeTag.tpe.dealias match {
              case f if f <:< typeOf[FloatType] =>
                Op.OpFAdd
              case i if i <:< typeOf[IntegerType] =>
                Op.OpIAdd
            }
            val instructions = List(
              Instruction(addOpcode, List(
                ResultRef(ctx.nextResultId),
                ResultRef(ctx.exprRefs(expr.dependencies(0).digest)),
                ResultRef(ctx.exprRefs(expr.dependencies(1).digest))
              ))
            )
            val updatedContext = ctx.copy(
              exprRefs = ctx.exprRefs + (expr.digest -> ctx.nextResultId),
              nextResultId = ctx.nextResultId + 1
            )
            (instructions, updatedContext)

          case mul@Mul(a, b) =>
            val addOpcode = a.valTypeTag.tpe.dealias match {
              case f if f <:< typeOf[FloatType] =>
                Op.OpFMul
              case i if i <:< typeOf[IntegerType] =>
                Op.OpIMul
            }
            val instructions = List(
              Instruction(addOpcode, List(
                ResultRef(ctx.nextResultId),
                ResultRef(ctx.exprRefs(expr.dependencies(0).digest)),
                ResultRef(ctx.exprRefs(expr.dependencies(1).digest))
              ))
            )
            val updatedContext = ctx.copy(
              exprRefs = ctx.exprRefs + (expr.digest -> ctx.nextResultId),
              nextResultId = ctx.nextResultId + 1
            )
            (instructions, updatedContext)

          case div@Div(a, b) =>
            val addOpcode = a.valTypeTag.tpe.dealias match {
              case f if f <:< typeOf[FloatType] =>
                Op.OpFDiv
              case i if i <:< typeOf[IntegerType] =>
                Op.OpSDiv
            }
            val instructions = List(
              Instruction(addOpcode, List(
                ResultRef(ctx.nextResultId),
                ResultRef(ctx.exprRefs(expr.dependencies(0).digest)),
                ResultRef(ctx.exprRefs(expr.dependencies(1).digest))
              ))
            )
            val updatedContext = ctx.copy(
              exprRefs = ctx.exprRefs + (expr.digest -> ctx.nextResultId),
              nextResultId = ctx.nextResultId + 1
            )
            (instructions, updatedContext)

          case ga@GArrayElem(index, i) =>
            val instructions = List(
              Instruction(Op.OpAccessChain, List(
                ResultRef(ctx.nextResultId),
                ResultRef(ctx.inBufferBlocks(index).blockPointerRef),
                ResultRef(ctx.inBufferBlocks(index).blockVarRef),
                ResultRef(ctx.constRefs((typeOf[Int32], 0))),
                ResultRef(ctx.exprRefs(expr.dependencies.head.digest))
              )),
              Instruction(Op.OpLoad, List(
                ResultRef(ctx.nextResultId + 1),
                IntWord(ctx.scalarTypeMap(ga.valTypeTag.tpe.dealias)),
                ResultRef(ctx.nextResultId)
              ))
            )
            val updatedContext = ctx.copy(
              exprRefs = ctx.exprRefs + (expr.digest -> (ctx.nextResultId + 1)),
              nextResultId = ctx.nextResultId + 2
            )
            (instructions, updatedContext)
        }
        println(ctx.exprRefs)
        compileExpressions(exprs.tail, updatedCtx, acc ::: instructions)
      }
    }
    compileExpressions(sortedTree, ctx, List())
  }

  def compileMain(sortedTree: List[DigestedExpression], ctx: Context): List[Words] = {
    val init = List(
      Instruction(Op.OpFunction, List(
        ResultRef(MAIN_FUNC_REF),
        ResultRef(ctx.voidTypeRef),
        SamplerAddressingMode.None,
        ResultRef(VOID_FUNC_TYPE_REF)
      ))
    )

    val (body, codeCtx) = compileTree(sortedTree, ctx)

    val end = List(
      Instruction(Op.OpAccessChain, List(
        ResultRef(codeCtx.nextResultId),
        ResultRef(codeCtx.outBufferBlocks(0).blockPointerRef),
        ResultRef(codeCtx.outBufferBlocks(0).blockVarRef),
        ResultRef(codeCtx.constRefs((typeOf[Int32], 0))),
        ResultRef(codeCtx.exprRefs(sortedTree.last.digest))
      )),
      Instruction(Op.OpStore, List(
        ResultRef(codeCtx.nextResultId),
        IntWord(codeCtx.scalarTypeMap(sortedTree.last.expr.valTypeTag.tpe.dealias))
      )),
      Instruction(Op.OpReturn, List()),
      Instruction(Op.OpFunctionEnd, List())
    )
    init ::: body ::: end
  }

  def compile[T<: ValType](returnVal: T, inTypes: List[Type], outTypes: List[Type]): ByteBuffer = {
    val tree = returnVal.tree
    val (digestTree, hash) = Digest.digest(tree)
    val sorted = TopologicalSort.sortTree(digestTree)
    println(Digest.formatTreeWithDigest(digestTree))
    println()
    println(sorted.mkString("\n"))

    val insnWithHeader = headers()
    val typesInCode = sorted.map(_.expr.valTypeTag.tpe.dealias)
    val allTypes = (typesInCode ::: inTypes ::: outTypes)
    val (typeDefs, typedContext) = defineTypes(allTypes)
    val (decorations, uniformDefs, uniformContext) = initAndDecorateUniforms(inTypes, outTypes, typedContext)
    val (inputDefs, inputContext) = createInvocationId(uniformContext)
    val (constDefs, constCtx) = defineConstants(sorted, inputContext)
    println("preC")
    val main = compileMain(sorted, constCtx)

    val code =
        insnWithHeader :::
        decorations :::
        typeDefs :::
        uniformDefs :::
        inputDefs :::
        constDefs :::
        main

    println("XD")
    hexDumpCode(code)
    dumpCode(code)
    println(inputContext)
    BufferUtils.createByteBuffer(1)
  }

  def hexDumpCode(code: List[Words]): Unit = {
    println(code.flatMap(_.toWords)
      .grouped(4).map(_.map(i => Integer.toHexString(i & 0xFF))
      .mkString(" ")).mkString("\n"))
  }

  def dumpCode(code: List[Words]): Unit = {
    println(code.map(_.toString).mkString("\n"));
  }
}
