package com.unihogsoft.scalag.cl;

public class Opcodes {
  public static int OpUndef = 1;
  public static int OpSizeOf = 321;
  public static int OpSourceContinued = 2;
  public static int OpSource = 3;
  public static int OpSourceExtension = 4;
  public static int OpName = 5;
  public static int OpMemberName = 6;
  public static int OpString = 7;
  public static int OpLine = 8;
  public static int OpNoLine = 317;
  public static int OpModuleProcessed = 330;
  public static int OpDecorate = 71;
  public static int OpMemberDecorate = 72;
  public static int OpDecorationGroup = 73;
  public static int OpGroupDecorate = 74;
  public static int OpGroupMemberDecorate = 75;
  public static int OpExtension = 10;
  public static int OpExtInstImport = 11;
  public static int OpExtInst = 12;
  public static int OpMemoryModel = 14;
  public static int OpEntryPoint = 15;
  public static int OpExecutionMode = 16;
  public static int OpCapability = 17;
  public static int OpTypeVoid = 19;
  public static int OpTypeBool = 20;
  public static int OpTypeInt = 21;
  public static int OpTypeFloat = 22;
  public static int OpTypeVector = 23;
  public static int OpTypeMatrix = 24;
  public static int OpTypeImage = 25;
  public static int OpTypeSampler = 26;
  public static int OpTypeSampledImage = 27;
  public static int OpTypeArray = 28;
  public static int OpTypeRuntimeArray = 29;
  public static int OpTypeStruct = 30;
  public static int OpTypeOpaque = 31;
  public static int OpTypePointer = 32;
  public static int OpTypeFunction = 33;
  public static int OpTypeEvent = 34;
  public static int OpTypeDeviceEvent = 35;
  public static int OpTypeReserveId = 36;
  public static int OpTypeQueue = 37;
  public static int OpTypePipe = 38;
  public static int OpTypeForwardPointer = 39;
  public static int OpTypePipeStorage = 322;
  public static int OpTypeNamedBarrier = 327;
  public static int OpConstantTrue = 41;
  public static int OpConstantFalse = 42;
  public static int OpConstant = 43;
  public static int OpConstantComposite = 44;
  public static int OpConstantSampler = 45;
  public static int OpConstantNull = 46;
  public static int OpSpecConstantTrue = 48;
  public static int OpSpecConstantFalse = 49;
  public static int OpSpecConstant = 50;
  public static int OpSpecConstantComposite = 51;
  public static int OpSpecConstantOp = 52;
  public static int OpVariable = 59;
  public static int OpImageTexelPointer = 60;
  public static int OpLoad = 61;
  public static int OpStore = 62;
  public static int OpCopyMemory = 63;
  public static int OpCopyMemorySized = 64;
  public static int OpAccessChain = 65;
  public static int OpInBoundsAccessChain = 66;
  public static int OpPtrAccessChain = 67;
  public static int OpArrayLength = 68;
  public static int OpGenericPtrMemSemantics = 69;
  public static int OpInBoundsPtrAccessChain = 70;
  public static int OpFunction = 54;
  public static int OpFunctionParameter = 55;
  public static int OpFunctionEnd = 56;
  public static int OpFunctionCall = 57;
  public static int OpSampledImage = 86;
  public static int OpImageSampleImplicitLod = 87;
  public static int OpImageSampleExplicitLod = 88;
  public static int OpImageSampleDrefImplicitLod = 89;
  public static int OpImageSampleDrefExplicitLod = 90;
  public static int OpImageSampleProjImplicitLod = 91;
  public static int OpImageSampleProjExplicitLod = 92;
  public static int OpImageSampleProjDrefImplicitLod = 93;
  public static int OpImageSampleProjDrefExplicitLod = 94;
  public static int OpImageFetch = 95;
  public static int OpImageGather = 96;
  public static int OpImageDrefGather = 97;
  public static int OpImageRead = 98;
  public static int OpImageWrite = 99;
  public static int OpImage = 100;
  public static int OpImageQueryFormat = 101;
  public static int OpImageQueryOrder = 102;
  public static int OpImageQuerySizeLod = 103;
  public static int OpImageQuerySize = 104;
  public static int OpImageQueryLod = 105;
  public static int OpImageQueryLevels = 106;
  public static int OpImageQuerySamples = 107;
  public static int OpImageSparseSampleImplicitLod = 305;
  public static int OpImageSparseSampleExplicitLod = 306;
  public static int OpImageSparseSampleDrefImplicitLod = 307;
  public static int OpImageSparseSampleDrefExplicitLod = 308;
  public static int OpImageSparseSampleProjImplicitLod = 309;
  public static int OpImageSparseSampleProjExplicitLod = 310;
  public static int OpImageSparseSampleProjDrefImplicitLod = 311;
  public static int OpImageSparseSampleProjDrefExplicitLod = 312;
  public static int OpImageSparseFetch = 313;
  public static int OpImageSparseGather = 314;
  public static int OpImageSparseDrefGather = 315;
  public static int OpImageSparseTexelsResident = 316;
  public static int OpImageSparseRead = 320;
  public static int OpConvertFToU = 109;
  public static int OpConvertFToS = 110;
  public static int OpConvertSToF = 111;
  public static int OpConvertUToF = 112;
  public static int OpUConvert = 113;
  public static int OpSConvert = 114;
  public static int OpFConvert = 115;
  public static int OpQuantizeToF16 = 116;
  public static int OpConvertPtrToU = 117;
  public static int OpSatConvertSToU = 118;
  public static int OpSatConvertUToS = 119;
  public static int OpConvertUToPtr = 120;
  public static int OpPtrCastToGeneric = 121;
  public static int OpGenericCastToPtr = 122;
  public static int OpGenericCastToPtrExplicit = 123;
  public static int OpBitcast = 124;
  public static int OpVectorExtractDynamic = 77;
  public static int OpVectorInsertDynamic = 78;
  public static int OpVectorShuffle = 79;
  public static int OpCompositeConstruct = 80;
  public static int OpCompositeExtract = 81;
  public static int OpCompositeInsert = 82;
  public static int OpCopyObject = 83;
  public static int OpTranspose = 84;
  public static int OpSNegate = 126;
  public static int OpFNegate = 127;
  public static int OpIAdd = 128;
  public static int OpFAdd = 129;
  public static int OpISub = 130;
  public static int OpFSub = 131;
  public static int OpIMul = 132;
  public static int OpFMul = 133;
  public static int OpUDiv = 134;
  public static int OpSDiv = 135;
  public static int OpFDiv = 136;
  public static int OpUMod = 137;
  public static int OpSRem = 138;
  public static int OpSMod = 139;
  public static int OpFRem = 140;
  public static int OpFMod = 141;
  public static int OpVectorTimesScalar = 142;
  public static int OpMatrixTimesScalar = 143;
  public static int OpVectorTimesMatrix = 144;
  public static int OpMatrixTimesVector = 145;
  public static int OpMatrixTimesMatrix = 146;
  public static int OpOuterProduct = 147;
  public static int OpDot = 148;
  public static int OpIAddCarry = 149;
  public static int OpISubBorrow = 150;
  public static int OpUMulExtended = 151;
  public static int OpSMulExtended = 152;
  public static int OpShiftRightLogical = 194;
  public static int OpShiftRightArithmetic = 195;
  public static int OpShiftLeftLogical = 196;
  public static int OpBitwiseOr = 197;
  public static int OpBitwiseXor = 198;
  public static int OpBitwiseAnd = 199;
  public static int OpNot = 200;
  public static int OpBitFieldInsert = 201;
  public static int OpBitFieldSExtract = 202;
  public static int OpBitFieldUExtract = 203;
  public static int OpBitReverse = 204;
  public static int OpBitCount = 205;
  public static int OpAny = 154;
  public static int OpAll = 155;
  public static int OpIsNan = 156;
  public static int OpIsInf = 157;
  public static int OpIsFinite = 158;
  public static int OpIsNormal = 159;
  public static int OpSignBitSet = 160;
  public static int OpLessOrGreater = 161;
  public static int OpOrdered = 162;
  public static int OpUnordered = 163;
  public static int OpLogicalEqual = 164;
  public static int OpLogicalNotEqual = 165;
  public static int OpLogicalOr = 166;
  public static int OpLogicalAnd = 167;
  public static int OpLogicalNot = 168;
  public static int OpSelect = 169;
  public static int OpIEqual = 170;
  public static int OpINotEqual = 171;
  public static int OpUGreaterThan = 172;
  public static int OpSGreaterThan = 173;
  public static int OpUGreaterThanEqual = 174;
  public static int OpSGreaterThanEqual = 175;
  public static int OpULessThan = 176;
  public static int OpSLessThan = 177;
  public static int OpULessThanEqual = 178;
  public static int OpSLessThanEqual = 179;
  public static int OpFOrdEqual = 180;
  public static int OpFUnordEqual = 181;
  public static int OpFOrdNotEqual = 182;
  public static int OpFUnordNotEqual = 183;
  public static int OpFOrdLessThan = 184;
  public static int OpFUnordLessThan = 185;
  public static int OpFOrdGreaterThan = 186;
  public static int OpFUnordGreaterThan = 187;
  public static int OpFOrdLessThanEqual = 188;
  public static int OpFUnordLessThanEqual = 189;
  public static int OpFOrdGreaterThanEqual = 190;
  public static int OpFUnordGreaterThanEqual = 191;
  public static int OpDPdx = 207;
  public static int OpDPdy = 208;
  public static int OpFwidth = 209;
  public static int OpDPdxFine = 210;
  public static int OpDPdyFine = 211;
  public static int OpFwidthFine = 212;
  public static int OpDPdxCoarse = 213;
  public static int OpDPdyCoarse = 214;
  public static int OpFwidthCoarse = 215;
  public static int OpPhi = 245;
  public static int OpLoopMerge = 246;
  public static int OpSelectionMerge = 247;
  public static int OpLabel = 248;
  public static int OpBranch = 249;
  public static int OpBranchConditional = 250;
  public static int OpSwitch = 251;
  public static int OpKill = 252;
  public static int OpReturn = 253;
  public static int OpReturnValue = 254;
  public static int OpUnreachable = 255;
  public static int OpLifetimeStart = 256;
  public static int OpLifetimeStop = 257;
  public static int OpAtomicLoad = 227;
  public static int OpAtomicStore = 228;
  public static int OpAtomicExchange = 229;
  public static int OpAtomicCompareExchange = 230;
  public static int OpAtomicCompareExchangeWeak = 231;
  public static int OpAtomicIIncrement = 232;
  public static int OpAtomicIDecrement = 233;
  public static int OpAtomicIAdd = 234;
  public static int OpAtomicISub = 235;
  public static int OpAtomicSMin = 236;
  public static int OpAtomicUMin = 237;
  public static int OpAtomicSMax = 238;
  public static int OpAtomicUMax = 239;
  public static int OpAtomicAnd = 240;
  public static int OpAtomicOr = 241;
  public static int OpAtomicXor = 242;
  public static int OpAtomicFlagTestAndSet = 318;
  public static int OpAtomicFlagClear = 319;
  public static int OpEmitVertex = 218;
  public static int OpEndPrimitive = 219;
  public static int OpEmitStreamVertex = 220;
  public static int OpEndStreamPrimitive = 221;
  public static int OpControlBarrier = 224;
  public static int OpMemoryBarrier = 225;
  public static int OpNamedBarrierInitialize = 328;
  public static int OpMemoryNamedBarrier = 329;
  public static int OpGroupAsyncCopy = 259;
  public static int OpGroupWaitEvents = 260;
  public static int OpGroupAll = 261;
  public static int OpGroupAny = 262;
  public static int OpGroupBroadcast = 263;
  public static int OpGroupIAdd = 264;
  public static int OpGroupFAdd = 265;
  public static int OpGroupFMin = 266;
  public static int OpGroupUMin = 267;
  public static int OpGroupSMin = 268;
  public static int OpGroupFMax = 269;
  public static int OpGroupUMax = 270;
  public static int OpGroupSMax = 271;
  public static int OpSubgroupBallotKHR = 4421;
  public static int OpSubgroupFirstInvocationKHR = 4422;
  public static int OpSubgroupReadInvocationKHR = 4432;
  public static int OpGroupIAddNonUniformAMD = 5000;
  public static int OpGroupFAddNonUniformAMD = 5001;
  public static int OpGroupFMinNonUniformAMD = 5002;
  public static int OpGroupUMinNonUniformAMD = 5003;
  public static int OpGroupSMinNonUniformAMD = 5004;
  public static int OpGroupFMaxNonUniformAMD = 5005;
  public static int OpGroupUMaxNonUniformAMD = 5006;
  public static int OpGroupSMaxNonUniformAMD = 5007;
  public static int OpEnqueueMarker = 291;
  public static int OpEnqueueKernel = 292;
  public static int OpGetKernelNDrangeSubGroupCount = 293;
  public static int OpGetKernelNDrangeMaxSubGroupSize = 294;
  public static int OpGetKernelWorkGroupSize = 295;
  public static int OpGetKernelPreferredWorkGroupSizeMultiple = 296;
  public static int OpRetainEvent = 297;
  public static int OpReleaseEvent = 298;
  public static int OpCreateUserEvent = 299;
  public static int OpIsValidEvent = 300;
  public static int OpSetUserEventStatus = 301;
  public static int OpCaptureEventProfilingInfo = 302;
  public static int OpGetDefaultQueue = 303;
  public static int OpBuildNDRange = 304;
  public static int OpGetKernelLocalSizeForSubgroupCount = 325;
  public static int OpGetKernelMaxNumSubgroups = 326;
  public static int OpReadPipe = 274;
  public static int OpWritePipe = 275;
  public static int OpReservedReadPipe = 276;
  public static int OpReservedWritePipe = 277;
  public static int OpReserveReadPipePackets = 278;
  public static int OpReserveWritePipePackets = 279;
  public static int OpCommitReadPipe = 280;
  public static int OpCommitWritePipe = 281;
  public static int OpIsValidReserveId = 282;
  public static int OpGetNumPipePackets = 283;
  public static int OpGetMaxPipePackets = 284;
  public static int OpGroupReserveReadPipePackets = 285;
  public static int OpGroupReserveWritePipePackets = 286;
  public static int OpGroupCommitReadPipe = 287;
  public static int OpGroupCommitWritePipe = 288;
  public static int OpConstantPipeStorage = 323;
  public static int OpCreatePipeFromPipeStorage = 324;
}