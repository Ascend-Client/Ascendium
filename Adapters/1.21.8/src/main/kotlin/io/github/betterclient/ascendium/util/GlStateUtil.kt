package io.github.betterclient.ascendium.util

import org.lwjgl.opengl.GL11C
import org.lwjgl.opengl.GL13C
import org.lwjgl.opengl.GL14C
import org.lwjgl.opengl.GL20C
import org.lwjgl.opengl.GL21C
import org.lwjgl.opengl.GL30C
import org.lwjgl.opengl.GL33C
import org.lwjgl.system.MemoryStack

object GlStateUtil {
    private lateinit var savedState: State

    fun save() {
        savedState = State.capture()
    }

    fun restore() {
        savedState.restore()
    }

    private class State(
        val blendEnabled: Boolean,
        val blendSrcRgb: Int,
        val blendDstRgb: Int,
        val blendSrcAlpha: Int,
        val blendDstAlpha: Int,
        val depthTestEnabled: Boolean,
        val depthMask: Boolean,
        val depthFunc: Int,
        val cullEnabled: Boolean,
        val cullFace: Int,
        val activeTexture: Int,
        val textureBindings2D: IntArray,
        val samplerBindings: IntArray,
        val program: Int,
        val vaoBinding: Int,
        val colorMaskR: Boolean,
        val colorMaskG: Boolean,
        val colorMaskB: Boolean,
        val colorMaskA: Boolean,
        val unpackAlignment: Int,
        val pixelUnpackBufferBinding: Int,
        val scissorTestEnabled: Boolean,
        val scissorBox: IntArray
    ) {
        fun restore() {
            if (blendEnabled) {
                GL11C.glEnable(GL11C.GL_BLEND)
            } else {
                GL11C.glDisable(GL11C.GL_BLEND)
            }
            GL14C.glBlendFuncSeparate(blendSrcRgb, blendDstRgb, blendSrcAlpha, blendDstAlpha)

            if (depthTestEnabled) {
                GL11C.glEnable(GL11C.GL_DEPTH_TEST)
            } else {
                GL11C.glDisable(GL11C.GL_DEPTH_TEST)
            }
            GL11C.glDepthMask(depthMask)
            GL11C.glDepthFunc(depthFunc)

            if (cullEnabled) {
                GL11C.glEnable(GL11C.GL_CULL_FACE)
            } else {
                GL11C.glDisable(GL11C.GL_CULL_FACE)
            }
            GL11C.glCullFace(cullFace)

            for (i in textureBindings2D.indices) {
                GL13C.glActiveTexture(GL13C.GL_TEXTURE0 + i)
                GL11C.glBindTexture(GL11C.GL_TEXTURE_2D, textureBindings2D[i])
                GL33C.glBindSampler(i, samplerBindings[i])
            }
            GL13C.glActiveTexture(activeTexture)

            GL20C.glUseProgram(program)

            GL30C.glBindVertexArray(vaoBinding)

            GL11C.glColorMask(colorMaskR, colorMaskG, colorMaskB, colorMaskA)
            GL11C.glPixelStorei(GL11C.GL_UNPACK_ALIGNMENT, unpackAlignment)
            GL21C.glBindBuffer(GL21C.GL_PIXEL_UNPACK_BUFFER, pixelUnpackBufferBinding)

            if (scissorTestEnabled) {
                GL11C.glEnable(GL11C.GL_SCISSOR_TEST)
            } else {
                GL11C.glDisable(GL11C.GL_SCISSOR_TEST)
            }
            GL11C.glScissor(scissorBox[0], scissorBox[1], scissorBox[2], scissorBox[3])
        }

        companion object {
            fun capture(): State {
                MemoryStack.stackPush().use { stack ->
                    val maxTextureUnits = GL11C.glGetInteger(GL20C.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS)
                    val textureBindings = IntArray(maxTextureUnits)
                    val samplerBindings = IntArray(maxTextureUnits)
                    val activeTexture = GL11C.glGetInteger(GL13C.GL_ACTIVE_TEXTURE)

                    for (i in 0 until maxTextureUnits) {
                        GL13C.glActiveTexture(GL13C.GL_TEXTURE0 + i)
                        textureBindings[i] = GL11C.glGetInteger(GL11C.GL_TEXTURE_BINDING_2D)
                        samplerBindings[i] = GL11C.glGetInteger(GL33C.GL_SAMPLER_BINDING)
                    }
                    GL13C.glActiveTexture(activeTexture)

                    val colorMask = stack.malloc(4)
                    GL11C.glGetBooleanv(GL11C.GL_COLOR_WRITEMASK, colorMask)

                    val blendEnabled = GL11C.glIsEnabled(GL11C.GL_BLEND)
                    val blendSrcRgb = GL11C.glGetInteger(GL14C.GL_BLEND_SRC_RGB)
                    val blendDstRgb = GL11C.glGetInteger(GL14C.GL_BLEND_DST_RGB)
                    val blendSrcAlpha = GL11C.glGetInteger(GL14C.GL_BLEND_SRC_ALPHA)
                    val blendDstAlpha = GL11C.glGetInteger(GL14C.GL_BLEND_DST_ALPHA)

                    val depthTestEnabled = GL11C.glIsEnabled(GL11C.GL_DEPTH_TEST)
                    val depthMask = GL11C.glGetBoolean(GL11C.GL_DEPTH_WRITEMASK)
                    val depthFunc = GL11C.glGetInteger(GL11C.GL_DEPTH_FUNC)

                    val cullEnabled = GL11C.glIsEnabled(GL11C.GL_CULL_FACE)
                    val cullFace = GL11C.glGetInteger(GL11C.GL_CULL_FACE_MODE)

                    val program = GL11C.glGetInteger(GL20C.GL_CURRENT_PROGRAM)
                    val vaoBinding = GL11C.glGetInteger(GL30C.GL_VERTEX_ARRAY_BINDING)
                    val unpackAlignment = GL11C.glGetInteger(GL11C.GL_UNPACK_ALIGNMENT)
                    val pixelUnpackBufferBinding = GL11C.glGetInteger(GL21C.GL_PIXEL_UNPACK_BUFFER_BINDING)

                    val scissorTestEnabled = GL11C.glIsEnabled(GL11C.GL_SCISSOR_TEST)
                    val scissorBox = stack.mallocInt(4)
                    GL11C.glGetIntegerv(GL11C.GL_SCISSOR_BOX, scissorBox)

                    return State(
                        blendEnabled,
                        blendSrcRgb, blendDstRgb, blendSrcAlpha, blendDstAlpha,
                        depthTestEnabled, depthMask, depthFunc,
                        cullEnabled, cullFace,
                        activeTexture, textureBindings, samplerBindings,
                        program, vaoBinding,
                        colorMask[0].toInt() != 0, colorMask[1].toInt() != 0,
                        colorMask[2].toInt() != 0, colorMask[3].toInt() != 0,
                        unpackAlignment,
                        pixelUnpackBufferBinding,
                        scissorTestEnabled,
                        intArrayOf(scissorBox[0], scissorBox[1], scissorBox[2], scissorBox[3])
                    )
                }
            }
        }
    }
}