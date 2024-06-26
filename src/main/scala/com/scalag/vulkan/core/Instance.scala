package com.scalag.vulkan.core;

import com.scalag.vulkan.utility.VulkanAssertionError;
import com.scalag.vulkan.utility.VulkanObject;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkApplicationInfo;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkInstanceCreateInfo;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.scalag.vulkan.VulkanContext.VALIDATION_LAYERS;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.vulkan.EXTDebugReport.VK_EXT_DEBUG_REPORT_EXTENSION_NAME;
import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK11.VK_API_VERSION_1_1;

/**
 * @author MarconZet
 * Created 13.04.2020
 */
class Instance(enableValidationLayers: Boolean) extends VulkanObject {

    private static final String[] VALIDATION_LAYERS_INSTANCE_EXTENSIONS = {VK_EXT_DEBUG_REPORT_EXTENSION_NAME};
    private static final String[] INSTANCE_EXTENSIONS = {};


    private VkInstance instance;

    override     protected void init() {
        try (MemoryStack stack = stackPush()) {
            VkApplicationInfo appInfo = VkApplicationInfo.callocStack()
                    .sType(VK_STRUCTURE_TYPE_APPLICATION_INFO)
                    .pNext(NULL)
                    .pApplicationName(stack.UTF8("ScalaG MVP"))
                    .pEngineName(stack.UTF8("ScalaG Computing Engine"))
                    .applicationVersion(VK_MAKE_VERSION(0, 1, 0))
                    .engineVersion(VK_MAKE_VERSION(0, 1, 0))
                    .apiVersion(VK_API_VERSION_1_1);

            PointerBuffer ppEnabledExtensionNames = getInstanceExtensions(stack);
            PointerBuffer ppEnabledLayerNames = getValidationLayers(stack);

            VkInstanceCreateInfo pCreateInfo = VkInstanceCreateInfo.callocStack()
                    .sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO)
                    .pNext(NULL)
                    .pApplicationInfo(appInfo)
                    .ppEnabledExtensionNames(ppEnabledExtensionNames)
                    .ppEnabledLayerNames(ppEnabledLayerNames);
            PointerBuffer pInstance = stack.mallocPointer(1);
            int err = vkCreateInstance(pCreateInfo, null, pInstance);
            long instance = pInstance.get(0);
            if (err != VK_SUCCESS) {
                throw new VulkanAssertionError("Failed to create VkInstance", err);
            }
            this.instance = new VkInstance(instance, pCreateInfo);
        }
    }

    private PointerBuffer getValidationLayers(MemoryStack stack) {
        if (enableValidationLayers) {
            PointerBuffer validationLayers = stack.callocPointer(VALIDATION_LAYERS.length);
            for (String layer : VALIDATION_LAYERS) {
                validationLayers.put(stack.ASCII(layer));
            }
            return validationLayers.flip();
        } else {
            return null;
        }
    }

    private PointerBuffer getInstanceExtensions(MemoryStack stack) {
        List<String> extensions = new LinkedList<>(Arrays.asList(INSTANCE_EXTENSIONS));
        if (enableValidationLayers)
            extensions.addAll(Arrays.asList(VALIDATION_LAYERS_INSTANCE_EXTENSIONS));

        PointerBuffer ppEnabledExtensionNames = stack.callocPointer(extensions.size());
        extensions.forEach(x -> ppEnabledExtensionNames.put(stack.ASCII(x)));
        return ppEnabledExtensionNames.flip();
    }

    override     protected void close() {
        vkDestroyInstance(instance, null);
    }

    VkInstance get() {
        return instance;
    }
}
