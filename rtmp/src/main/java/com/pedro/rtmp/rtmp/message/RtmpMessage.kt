package com.pedro.rtmp.rtmp.message

import com.pedro.rtmp.rtmp.chunk.ChunkStreamId
import com.pedro.rtmp.rtmp.chunk.ChunkType
import com.pedro.rtmp.rtmp.message.command.CommandAmf0
import com.pedro.rtmp.rtmp.message.command.CommandAmf3
import com.pedro.rtmp.rtmp.message.control.UserControl
import com.pedro.rtmp.rtmp.message.data.DataAmf0
import com.pedro.rtmp.rtmp.message.data.DataAmf3
import com.pedro.rtmp.rtmp.message.shared.SharedObjectAmf0
import com.pedro.rtmp.rtmp.message.shared.SharedObjectAmf3
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * Created by pedro on 20/04/21.
 */
abstract class RtmpMessage(var header: RtmpHeader = RtmpHeader()) {

  companion object {

    private val TAG = "RtmpMessage"

    @Throws(IOException::class)
    fun getRtmpMessage(input: InputStream): RtmpMessage {
      val header = RtmpHeader.readHeader(input)
      val rtmpMessage = when (header.messageType) {
        MessageType.SET_CHUNK_SIZE -> SetChunkSize()
        MessageType.ABORT -> Abort(header)
        MessageType.ACKNOWLEDGEMENT -> Acknowledgement()
        MessageType.USER_CONTROL -> UserControl()
        MessageType.WINDOW_ACKNOWLEDGEMENT_SIZE -> WindowAcknowledgementSize()
        MessageType.SET_PEER_BANDWIDTH -> SetPeerBandwidth()
        MessageType.AUDIO -> Audio()
        MessageType.VIDEO -> Video()
        MessageType.DATA_AMF3 -> DataAmf3()
        MessageType.SHARED_OBJECT_AMF3 -> SharedObjectAmf3()
        MessageType.COMMAND_AMF3 -> CommandAmf3()
        MessageType.DATA_AMF0 -> DataAmf0()
        MessageType.SHARED_OBJECT_AMF0 -> SharedObjectAmf0()
        MessageType.COMMAND_AMF0 -> CommandAmf0()
        MessageType.AGGREGATE -> Aggregate()
        else -> throw IOException("Unimplemented message type: ${header.messageType}")
      }
      rtmpMessage.readBody(input)
      return rtmpMessage
    }

    fun getMarkType(type: Int): MessageType {
      return when (type) {
        MessageType.SET_CHUNK_SIZE.mark.toInt() -> MessageType.SET_CHUNK_SIZE
        MessageType.ABORT.mark.toInt() -> MessageType.ABORT
        MessageType.ACKNOWLEDGEMENT.mark.toInt() -> MessageType.ACKNOWLEDGEMENT
        MessageType.USER_CONTROL.mark.toInt() -> MessageType.USER_CONTROL
        MessageType.WINDOW_ACKNOWLEDGEMENT_SIZE.mark.toInt() -> MessageType.WINDOW_ACKNOWLEDGEMENT_SIZE
        MessageType.SET_PEER_BANDWIDTH.mark.toInt() -> MessageType.SET_PEER_BANDWIDTH
        MessageType.AUDIO.mark.toInt() -> MessageType.AUDIO
        MessageType.VIDEO.mark.toInt() -> MessageType.VIDEO
        MessageType.DATA_AMF3.mark.toInt() -> MessageType.DATA_AMF3
        MessageType.SHARED_OBJECT_AMF3.mark.toInt() -> MessageType.SHARED_OBJECT_AMF3
        MessageType.COMMAND_AMF3.mark.toInt() -> MessageType.COMMAND_AMF3
        MessageType.DATA_AMF0.mark.toInt() -> MessageType.DATA_AMF0
        MessageType.SHARED_OBJECT_AMF0.mark.toInt() -> MessageType.SHARED_OBJECT_AMF0
        MessageType.COMMAND_AMF0.mark.toInt() -> MessageType.COMMAND_AMF0
        MessageType.AGGREGATE.mark.toInt() -> MessageType.AGGREGATE
        else -> {
          throw IOException("Unknown rtmp message type: $type")
        }
      }
    }
  }

  fun writeHeader(output: OutputStream) {
    header.writeHeader(output)
  }

  fun writeBody(output: OutputStream) {
    val chunkSize = 128
    val bytes = storeBody()
    var pos = 0
    var length = getSize()

    while (length > chunkSize) {
      // Write packet for chunk
      output.write(bytes, pos, chunkSize)
      length -= chunkSize
      pos += chunkSize
      // Write header for remain chunk
      header.writeHeader(BasicHeader(ChunkType.TYPE_3, ChunkStreamId.OVER_CONNECTION), output)
    }
    output.write(bytes, pos, length)
  }

  abstract fun readBody(input: InputStream)

  abstract fun storeBody(): ByteArray

  abstract fun getType(): MessageType

  abstract fun getSize(): Int

  override fun toString(): String {
    return "RtmpMessage(header=$header)"
  }
}