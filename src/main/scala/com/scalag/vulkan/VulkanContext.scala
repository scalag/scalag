package com.scalag.vulkan

import com.scalag.vulkan.command.{CommandPool, Queue, StandardCommandPool}
import com.scalag.vulkan.core.{DebugCallback, Device, Instance}
import com.scalag.vulkan.memory.{Allocator, DescriptorPool}

/** @author
  *   MarconZet Created 13.04.2020
  */
object VulkanContext {
  final val ValidationLayer: String = "VK_LAYER_KHRONOS_validation"
}

class VulkanContext(val enableValidationLayers: Boolean = false) {
  private val validationLayers = enableValidationLayers || true

  val instance: Instance = new Instance(validationLayers)
  val debugCallback: DebugCallback = if (validationLayers) new DebugCallback(instance) else null
  val device: Device = new Device(instance)
  val computeQueue: Queue = new Queue(device.computeQueueFamily, 0, device)
  val allocator: Allocator = new Allocator(instance, device)
  val descriptorPool: DescriptorPool = new DescriptorPool(device)
  val commandPool: CommandPool = new StandardCommandPool(device, computeQueue)

  def destroy(): Unit = {
    commandPool.destroy()
    descriptorPool.destroy()
    allocator.destroy()
    computeQueue.destroy()
    device.destroy()
    if (validationLayers)
      debugCallback.destroy()
    instance.destroy()
  }
}
