/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ratpackframework.util.internal;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public abstract class IoUtils {

  public static ByteBuf readFile(File file) throws IOException {
    FileInputStream fIn = null;
    FileChannel fChan = null;
    long fSize;
    ByteBuffer mBuf;

    try {
      fIn = new FileInputStream(file);
      fChan = fIn.getChannel();
      fSize = fChan.size();
      mBuf = ByteBuffer.allocate((int) fSize);
      fChan.read(mBuf);
      mBuf.rewind();
    } finally {
      if (fChan != null) {
        fChan.close();
      }
      if (fIn != null) {
        fIn.close();
      }
    }

    return Unpooled.wrappedBuffer(mBuf);
  }

  public static ByteBuf utf8Buffer(String str) {
    return byteBuf(utf8Bytes(str));
  }

  public static byte[] utf8Bytes(String str) {
    return str.getBytes(CharsetUtil.UTF_8);
  }

  public static String utf8String(ByteBuf byteBuf) {
    return byteBuf.toString(CharsetUtil.UTF_8);
  }

  public static ByteBuf byteBuf(byte[] bytes) {
    return Unpooled.wrappedBuffer(bytes);
  }

}
