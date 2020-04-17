package com.unihogsoft.scalag.vulkan;

import com.unihogsoft.scalag.vulkan.command.CommandPool;
import com.unihogsoft.scalag.vulkan.compute.MapPipeline;
import com.unihogsoft.scalag.vulkan.core.Device;
import com.unihogsoft.scalag.vulkan.memory.Allocator;
import com.unihogsoft.scalag.vulkan.memory.DescriptorPool;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.util.Objects;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author MarconZet
 * Created 17.04.2020
 */
class MapExecutorTest {

    private static VulkanContext context;

    private final static int bufferLength = 16384;
    private final static int bufferSize = 4 * bufferLength;

    @BeforeAll
    static void setUp() {
        context = new VulkanContext(true);
    }

    @AfterAll
    static void tearDown() {
        context.close();
    }

    @Test
    void execute() {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(Objects.requireNonNull(classLoader.getResource("comp.spv")).getFile());
            FileInputStream fis = new FileInputStream(file);
            FileChannel fc = fis.getChannel();
            ByteBuffer shader = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());


            Random rand = new Random(System.currentTimeMillis());
            ByteBuffer input = BufferUtils.createByteBuffer(bufferSize);
            IntBuffer input_buffer = input.asIntBuffer();
            while (input_buffer.hasRemaining()) {
                input_buffer.put(rand.nextInt());
            }
            input_buffer.flip();

            MapPipeline pipeline = new MapPipeline(shader, context);
            MapExecutor executor = new MapExecutor(bufferSize, bufferSize, bufferLength, pipeline, context);

            ByteBuffer result = executor.execute(input);

            IntBuffer test = result.asIntBuffer();

            while (test.hasRemaining()) {
                assertEquals(input_buffer.get(), test.get());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}