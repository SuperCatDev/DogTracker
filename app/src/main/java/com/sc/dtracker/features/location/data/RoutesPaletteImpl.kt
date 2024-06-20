package com.sc.dtracker.features.location.data

import android.content.Context
import androidx.core.content.ContextCompat
import com.sc.dtracker.R
import com.sc.dtracker.features.location.domain.RoutesPalette
import com.sc.dtracker.ui.ext.lazyUnsafe
import kotlin.random.Random
import kotlin.random.nextInt

class RoutesPaletteImpl(private val context: Context) : RoutesPalette {

    private val color1 by lazyUnsafe {
        ContextCompat.getColor(context, R.color.route_palette_1)
    }

    private val color2 by lazyUnsafe {
        ContextCompat.getColor(context, R.color.route_palette_2)
    }

    private val color3 by lazyUnsafe {
        ContextCompat.getColor(context, R.color.route_palette_3)
    }

    private val color4 by lazyUnsafe {
        ContextCompat.getColor(context, R.color.route_palette_4)
    }

    private val color5 by lazyUnsafe {
        ContextCompat.getColor(context, R.color.route_palette_5)
    }

    private val color6 by lazyUnsafe {
        ContextCompat.getColor(context, R.color.route_palette_6)
    }

    private val paletteSize = 6
    private var counter: Int = Random(hashCode()).nextInt(0 until paletteSize)

    override fun getColorFromPaletteFor(routeId: Int): Int {
        val next = (counter % paletteSize) + 1

        counter++

        return when (next) {
            1 -> color1
            2 -> color2
            3 -> color3
            4 -> color4
            5 -> color5
            6 -> color6
            else -> color1
        }
    }
}