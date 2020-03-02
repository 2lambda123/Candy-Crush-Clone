package j4k.candycrush.renderer

import com.soywiz.korge.view.*
import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korim.font.BitmapFont
import j4k.candycrush.LevelCheck
import j4k.candycrush.ScoreListener
import j4k.candycrush.lib.Loadable
import j4k.candycrush.lib.loadFont
import j4k.candycrush.lib.loadImage
import j4k.candycrush.math.PositionGrid
import j4k.candycrush.model.Tile

class LevelCheckRenderer(override val stage: Stage, val levelCheck: LevelCheck, val candies: CandySprites) : Loadable,
        ScoreListener {

    private val paddingLeft = 120
    private val paddingTop = 80
    private val paddingText = paddingTop + 55

    private val level = levelCheck.level
    private var moves: Text? = null
    private lateinit var font: BitmapFont
    private lateinit var moveArrow: Bitmap
    private var tileCounter = mutableListOf<TileCounter>()

    private class TileCounter(val tile: Tile, val count: Text)

    override suspend fun load() {
        font = loadFont("candy-small.fnt")
        moveArrow = loadImage("text_arrows_move.png")

        stage.image(moveArrow) {
            position(paddingLeft, paddingTop)
        }

        level.tileObjectives.forEachIndexed { index, objective ->
            val x = paddingLeft + 100 + (100 * index)
            val y = paddingTop
            stage.image(candies.getTile(objective.tile)) {
                position(x + 8, y)
                height = 64.0
                width = 64.0
            }
            val counter = stage.text(text = "88", textSize = 64.0, font = font) {
                position(x, paddingText)
            }
            tileCounter.add(TileCounter(objective.tile, counter))
        }
        updateMoves()
        updateCounters()
    }

    private fun updateCounters() {
        tileCounter.forEach { tileCounter ->
            val toString = levelCheck.getRemainingCount(tileCounter.tile).toString()
            tileCounter.count.text = toString
        }
    }

    private fun updateMoves() {
        val remainingMoves = levelCheck.remaining.toString()
        if (level.maxMoves != null) {
            val moves = this.moves
            if (moves == null) {
                this.moves = stage.text(text = remainingMoves, textSize = 64.0, font = font) {
                    position(paddingLeft, paddingText)
                }
            } else {
                moves.text = remainingMoves
            }
        }
    }

    override fun onScore(score: Int, multiplicator: Int, pos: PositionGrid.Position) {
        updateMoves()
        updateCounters()
    }
}