/*
 * Copyright 2016 Azavea
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package geotrellis.raster.io.geotiff

import geotrellis.raster._

import java.nio.ByteBuffer
import spire.syntax.cfor._


abstract class Float64GeoTiffSegment(val bytes: Array[Byte]) extends GeoTiffSegment {
  protected val buffer = ByteBuffer.wrap(bytes).asDoubleBuffer

  val size: Int = bytes.size / 8

  def get(i: Int): Double =
    buffer.get(i)

  def getInt(i: Int): Int
  def getDouble(i: Int): Double

  protected def intToDoubleOut(v: Int): Double
  protected def doubleToDoubleOut(v: Double): Double

  def map(f: Int => Int): Array[Byte] = {
    val arr = Array.ofDim[Double](size)
    cfor(0)(_ < size, _ + 1) { i =>
      arr(i) = i2d(f(getInt(i)))
    }
    val result = new Array[Byte](size * DoubleConstantNoDataCellType.bytes)
    val bytebuff = ByteBuffer.wrap(result)
    bytebuff.asDoubleBuffer.put(arr)
    result
  }

  def mapDouble(f: Double => Double): Array[Byte] = {
    val arr = Array.ofDim[Double](size)
    cfor(0)(_ < size, _ + 1) { i =>
      arr(i) = f(getDouble(i))
    }
    val result = new Array[Byte](size * DoubleConstantNoDataCellType.bytes)
    val bytebuff = ByteBuffer.wrap(result)
    bytebuff.asDoubleBuffer.put(arr)
    result
  }

  def mapWithIndex(f: (Int, Int) => Int): Array[Byte] = {
    val arr = Array.ofDim[Double](size)
    cfor(0)(_ < size, _ + 1) { i =>
      arr(i) = i2d(f(i, getInt(i)))
    }
    val result = new Array[Byte](size * DoubleConstantNoDataCellType.bytes)
    val bytebuff = ByteBuffer.wrap(result)
    bytebuff.asDoubleBuffer.put(arr)
    result
  }

  def mapDoubleWithIndex(f: (Int, Double) => Double): Array[Byte] = {
    val arr = Array.ofDim[Double](size)
    cfor(0)(_ < size, _ + 1) { i =>
      arr(i) = f(i, getDouble(i))
    }
    val result = new Array[Byte](size * DoubleConstantNoDataCellType.bytes)
    val bytebuff = ByteBuffer.wrap(result)
    bytebuff.asDoubleBuffer.put(arr)
    result
  }
}
