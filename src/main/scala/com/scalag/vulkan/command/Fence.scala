package com.scalag.vulkan.command;

import com.scalag.vulkan.core.Device;
import com.scalag.vulkan.utility.VulkanAssertionError;
import com.scalag.vulkan.utility.VulkanObjectHandle;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkFenceCreateInfo;

import java.nio.LongBuffer;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.*;

/**
 * @author MarconZet
 * Created 13.04.2020
 * Copied from Wrap
 */
class Fence extends VulkanObjectHandle {
    private Runnable runnable;
    int flags;

    private Device device;

    Fence(Device device) {
        this(device, 0);
    }

    Fence(Device device, int flags) {
        this.device = device;
        this.flags = flags;
        create();
    }

    Fence onDestroy(Runnable runnable) {
        this.runnable = runnable;
        return this;
    }

    override     protected void init() {
        try (MemoryStack stack = stackPush()) {
            VkFenceCreateInfo fenceInfo = VkFenceCreateInfo.callocStack()
                    .sType(VK_STRUCTURE_TYPE_FENCE_CREATE_INFO)
                    .pNext(VK_NULL_HANDLE)
                    .flags(flags);

            LongBuffer pFence = stack.callocLong(1);
            int err = vkCreateFence(device.get(), fenceInfo, null, pFence);
            if (err != VK_SUCCESS) {
                throw new VulkanAssertionError("Failed to create fence", err);
            }
            handle = pFence.get();
        }
    }

    override     void close() {
        if (runnable != null)
            runnable.run();
        vkDestroyFence(device.get(), handle, null);
    }

    boolean isSignaled() {
        int result = vkGetFenceStatus(device.get(), handle);
        if (!(result == VK_SUCCESS || result == VK_NOT_READY)) {
            throw new VulkanAssertionError("Failed to get fence status", result);
        }
        return result == VK_SUCCESS;
    }

    Fence reset() {
        vkResetFences(device.get(), handle);
        return this;
    }

    Fence block() {
        block(Long.MAX_VALUE);
        return this;
    }

    boolean block(long timeout) {
        int err = vkWaitForFences(device.get(), handle, true, timeout);
        if (err != VK_SUCCESS && err != VK_TIMEOUT) {
            throw new VulkanAssertionError("Failed to wait for fences", err);
        }
        return err == VK_SUCCESS;
    }
}
