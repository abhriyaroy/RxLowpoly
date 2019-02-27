package com.zebrostudio.lowpolyrxjava

import java.util.ArrayList
import java.util.Arrays

internal object DelaunayTriangularize {

  private val EPSILON = 1.0f / 1048576.0f

  private fun superTriangle(vertices: List<IntArray>): Array<IntArray> {
    var xmin = Integer.MAX_VALUE
    var ymin = Integer.MAX_VALUE
    var xmax = Integer.MIN_VALUE
    var ymax = Integer.MIN_VALUE

    val dx: Float
    val dy: Float
    val dmax: Float
    val xmid: Float
    val ymid: Float

    for (i in vertices.indices.reversed()) {
      val p = vertices[i]
      if (p[0] < xmin) xmin = p[0]
      if (p[0] > xmax) xmax = p[0]
      if (p[1] < ymin) ymin = p[1]
      if (p[1] > ymax) ymax = p[1]
    }

    dx = (xmax - xmin).toFloat()
    dy = (ymax - ymin).toFloat()

    dmax = Math.max(dx, dy)

    xmid = xmin + dx * 0.5f
    ymid = ymin + dy * 0.5f

    return arrayOf(
        intArrayOf((xmid - 20 * dmax).toInt(), (ymid - dmax).toInt()),
        intArrayOf(xmid.toInt(), (ymid + 20 * dmax).toInt()),
        intArrayOf((xmid + 20 * dmax).toInt(), (ymid - dmax).toInt())
    )
  }

  private fun circumcircle(vertices: List<IntArray>, i: Int, j: Int, k: Int): Circumcircle {
    val x1 = vertices[i][0]
    val y1 = vertices[i][1]
    val x2 = vertices[j][0]
    val y2 = vertices[j][1]
    val x3 = vertices[k][0]
    val y3 = vertices[k][1]

    val fabsy1y2 = Math.abs(y1 - y2)
    val fabsy2y3 = Math.abs(y2 - y3)

    val xc: Float
    val yc: Float
    val m1: Float
    val m2: Float
    val mx1: Float
    val mx2: Float
    val my1: Float
    val my2: Float
    val dx: Float
    val dy: Float

    if (fabsy1y2 <= 0) {
      m2 = -((x3 - x2).toFloat() / (y3 - y2))
      mx2 = (x2 + x3) / 2f
      my2 = (y2 + y3) / 2f
      xc = (x2 + x1) / 2f
      yc = m2 * (xc - mx2) + my2
    } else if (fabsy2y3 <= 0) {
      m1 = -((x2 - x1).toFloat() / (y2 - y1))
      mx1 = (x1 + x2) / 2f
      my1 = (y1 + y2) / 2f
      xc = (x3 + x2) / 2f
      yc = m1 * (xc - mx1) + my1
    } else {
      m1 = -((x2 - x1).toFloat() / (y2 - y1))
      m2 = -((x3 - x2).toFloat() / (y3 - y2))
      mx1 = (x1 + x2) / 2f
      mx2 = (x2 + x3) / 2f
      my1 = (y1 + y2) / 2f
      my2 = (y2 + y3) / 2f
      xc = (m1 * mx1 - m2 * mx2 + my2 - my1) / (m1 - m2)
      yc = if (fabsy1y2 > fabsy2y3)
        m1 * (xc - mx1) + my1
      else
        m2 * (xc - mx2) + my2
    }

    dx = x2 - xc
    dy = y2 - yc

    return Circumcircle(i, j, k, xc, yc, dx * dx + dy * dy)
  }

  private fun dedup(edges: ArrayList<Int>) {
    var a: Int
    var b: Int
    var m: Int
    var n: Int
    var j = edges.size
    while (j > 0) {
      while (j > edges.size) {
        j--
      }
      if (j <= 0) {
        break
      }
      b = edges[--j]
      a = edges[--j]

      var i = j
      while (i > 0) {
        n = edges[--i]
        m = edges[--i]

        if (a == m && b == n || a == n && b == m) {
          if (j + 1 < edges.size) {
            edges.removeAt(j + 1)
          }
          edges.removeAt(j)
          if (i + 1 < edges.size) {
            edges.removeAt(i + 1)
          }
          edges.removeAt(i)
          break
        }
      }
    }
  }

  fun triangulate(vertices: MutableList<IntArray>): List<Int> {
    val n = vertices.size

    if (n < 3) {
      return ArrayList()
    }

    val indices = arrayOfNulls<Int>(n)

    for (i in n - 1 downTo 0) {
      indices[i] = i
    }

    Arrays.sort<Int>(
        indices
    ) { lhs, rhs -> vertices[rhs!!][0] - vertices[lhs!!][0] }

    val st = superTriangle(vertices)

    vertices.add(st[0])
    vertices.add(st[1])
    vertices.add(st[2])

    val open = ArrayList<Circumcircle>()
    open.add(circumcircle(vertices, n, n + 1, n + 2))

    val closed = ArrayList<Circumcircle>()

    val edges = ArrayList<Int>()

    for (i in indices.indices.reversed()) {

      val c = indices[i]

      for (j in open.indices.reversed()) {

        val cj = open[j]
        val vj = vertices.get(c!!)

        val dx = vj[0] - cj.x

        if (dx > 0 && dx * dx > cj.r) {
          closed.add(cj)
          open.removeAt(j)
          continue
        }

        val dy = vj[1] - cj.y

        if (dx * dx + dy * dy - cj.r > EPSILON) {
          continue
        }

        edges.add(cj.i)
        edges.add(cj.j)
        edges.add(cj.j)
        edges.add(cj.k)
        edges.add(cj.k)
        edges.add(cj.i)

        open.removeAt(j)
      }

      dedup(edges)

      var j = edges.size
      while (j > 0) {
        val b = edges[--j]
        val a = edges[--j]
        open.add(circumcircle(vertices, a, b, c!!))
      }

      edges.clear()
    }

    for (i in open.indices.reversed()) {
      closed.add(open[i])
    }

    open.clear()

    val out = ArrayList<Int>()

    for (i in closed.indices.reversed()) {
      val ci = closed[i]
      if (ci.i < n && ci.j < n && ci.k < n) {
        out.add(ci.i)
        out.add(ci.j)
        out.add(ci.k)
      }
    }
    return out
  }

  private class Circumcircle(
    var i: Int,
    var j: Int,
    var k: Int,
    internal var x: Float,
    internal var y: Float,
    internal var r: Float
  )
}
