package com.pedro.rtmp.rtmp.message

import com.pedro.rtmp.rtmp.chunk.ChunkStreamId
import com.pedro.rtmp.rtmp.chunk.ChunkType
import java.io.InputStream

/**
 * Created by pedro on 21/04/21.
 */
class Audio: RtmpMessage(BasicHeader(ChunkType.TYPE_0, ChunkStreamId.AUDIO)) {

  override fun readBody(input: InputStream) {
    TODO("Not yet implemented")
  }

  override fun storeBody(): ByteArray {
    TODO("Not yet implemented")
  }

  override fun getType(): MessageType = MessageType.AUDIO

  override fun getSize(): Int {
    TODO("Not yet implemented")
  }
}