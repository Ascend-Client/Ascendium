package io.github.betterclient.ascendium.util.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.DefaultFillType
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

//Direct copies from material-icons-extended
object Icons {
    val Close: ImageVector
        get() {
            if (_close != null) {
                return _close!!
            }
            _close = materialIcon(name = "Filled.Close") {
                materialPath {
                    moveTo(19.0f, 6.41f)
                    lineTo(17.59f, 5.0f)
                    lineTo(12.0f, 10.59f)
                    lineTo(6.41f, 5.0f)
                    lineTo(5.0f, 6.41f)
                    lineTo(10.59f, 12.0f)
                    lineTo(5.0f, 17.59f)
                    lineTo(6.41f, 19.0f)
                    lineTo(12.0f, 13.41f)
                    lineTo(17.59f, 19.0f)
                    lineTo(19.0f, 17.59f)
                    lineTo(13.41f, 12.0f)
                    close()
                }
            }
            return _close!!
        }

    val ArrowBack: ImageVector
        get() {
            if (_arrowBack != null) {
                return _arrowBack!!
            }
            _arrowBack = materialIcon(name = "AutoMirrored.Filled.ArrowBack", autoMirror = true) {
                materialPath {
                    moveTo(20.0f, 11.0f)
                    horizontalLineTo(7.83f)
                    lineToRelative(5.59f, -5.59f)
                    lineTo(12.0f, 4.0f)
                    lineToRelative(-8.0f, 8.0f)
                    lineToRelative(8.0f, 8.0f)
                    lineToRelative(1.41f, -1.41f)
                    lineTo(7.83f, 13.0f)
                    horizontalLineTo(20.0f)
                    verticalLineToRelative(-2.0f)
                    close()
                }
            }
            return _arrowBack!!
        }

    val ArrowForward: ImageVector
        get() {
            if (_arrowForward != null) {
                return _arrowForward!!
            }
            _arrowForward = materialIcon(name = "AutoMirrored.Filled.ArrowForward", autoMirror = true) {
                materialPath {
                    moveTo(12.0f, 4.0f)
                    lineToRelative(-1.41f, 1.41f)
                    lineTo(16.17f, 11.0f)
                    horizontalLineTo(4.0f)
                    verticalLineToRelative(2.0f)
                    horizontalLineToRelative(12.17f)
                    lineToRelative(-5.58f, 5.59f)
                    lineTo(12.0f, 20.0f)
                    lineToRelative(8.0f, -8.0f)
                    close()
                }
            }
            return _arrowForward!!
        }

    val Home: ImageVector
        get() {
            if (_home != null) {
                return _home!!
            }
            _home = materialIcon(name = "Filled.Home") {
                materialPath {
                    moveTo(10.0f, 20.0f)
                    verticalLineToRelative(-6.0f)
                    horizontalLineToRelative(4.0f)
                    verticalLineToRelative(6.0f)
                    horizontalLineToRelative(5.0f)
                    verticalLineToRelative(-8.0f)
                    horizontalLineToRelative(3.0f)
                    lineTo(12.0f, 3.0f)
                    lineTo(2.0f, 12.0f)
                    horizontalLineToRelative(3.0f)
                    verticalLineToRelative(8.0f)
                    close()
                }
            }
            return _home!!
        }

    val Replay: ImageVector
        get() {
            if (_replay != null) {
                return _replay!!
            }
            _replay = materialIcon(name = "Filled.Replay") {
                materialPath {
                    moveTo(12.0f, 5.0f)
                    verticalLineTo(1.0f)
                    lineTo(7.0f, 6.0f)
                    lineToRelative(5.0f, 5.0f)
                    verticalLineTo(7.0f)
                    curveToRelative(3.31f, 0.0f, 6.0f, 2.69f, 6.0f, 6.0f)
                    reflectiveCurveToRelative(-2.69f, 6.0f, -6.0f, 6.0f)
                    reflectiveCurveToRelative(-6.0f, -2.69f, -6.0f, -6.0f)
                    horizontalLineTo(4.0f)
                    curveToRelative(0.0f, 4.42f, 3.58f, 8.0f, 8.0f, 8.0f)
                    reflectiveCurveToRelative(8.0f, -3.58f, 8.0f, -8.0f)
                    reflectiveCurveToRelative(-3.58f, -8.0f, -8.0f, -8.0f)
                    close()
                }
            }
            return _replay!!
        }

    val Check: ImageVector
        get() {
            if (_check != null) {
                return _check!!
            }
            _check = materialIcon(name = "Filled.Check") {
                materialPath {
                    moveTo(9.0f, 16.17f)
                    lineTo(4.83f, 12.0f)
                    lineToRelative(-1.42f, 1.41f)
                    lineTo(9.0f, 19.0f)
                    lineTo(21.0f, 7.0f)
                    lineToRelative(-1.41f, -1.41f)
                    close()
                }
            }
            return _check!!
        }

    val Add: ImageVector
        get() {
            if (_add != null) {
                return _add!!
            }
            _add = materialIcon(name = "Filled.Add") {
                materialPath {
                    moveTo(19.0f, 13.0f)
                    horizontalLineToRelative(-6.0f)
                    verticalLineToRelative(6.0f)
                    horizontalLineToRelative(-2.0f)
                    verticalLineToRelative(-6.0f)
                    horizontalLineTo(5.0f)
                    verticalLineToRelative(-2.0f)
                    horizontalLineToRelative(6.0f)
                    verticalLineTo(5.0f)
                    horizontalLineToRelative(2.0f)
                    verticalLineToRelative(6.0f)
                    horizontalLineToRelative(6.0f)
                    verticalLineToRelative(2.0f)
                    close()
                }
            }
            return _add!!
        }

    val Remove: ImageVector
        get() {
            if (_remove != null) {
                return _remove!!
            }
            _remove = materialIcon(name = "Filled.Remove") {
                materialPath {
                    moveTo(19.0f, 13.0f)
                    horizontalLineTo(5.0f)
                    verticalLineToRelative(-2.0f)
                    horizontalLineToRelative(14.0f)
                    verticalLineToRelative(2.0f)
                    close()
                }
            }
            return _remove!!
        }

    val ArrowDropDown: ImageVector
        get() {
            if (_arrowDropDown != null) {
                return _arrowDropDown!!
            }
            _arrowDropDown = materialIcon(name = "Filled.ArrowDropDown") {
                materialPath {
                    moveTo(7.0f, 10.0f)
                    lineToRelative(5.0f, 5.0f)
                    lineToRelative(5.0f, -5.0f)
                    close()
                }
            }
            return _arrowDropDown!!
        }

    private var _arrowDropDown: ImageVector? = null
    private var _remove: ImageVector? = null
    private var _add: ImageVector? = null
    private var _check: ImageVector? = null
    private var _replay: ImageVector? = null
    private var _home: ImageVector? = null
    private var _arrowForward: ImageVector? = null
    private var _arrowBack: ImageVector? = null
    private var _close: ImageVector? = null
}

inline fun materialIcon(
    name: String,
    autoMirror: Boolean = false,
    block: ImageVector.Builder.() -> ImageVector.Builder
): ImageVector = ImageVector.Builder(
    name = name,
    defaultWidth = MaterialIconDimension.dp,
    defaultHeight = MaterialIconDimension.dp,
    viewportWidth = MaterialIconDimension,
    viewportHeight = MaterialIconDimension,
    autoMirror = autoMirror
).block().build()

const val MaterialIconDimension = 24f

inline fun ImageVector.Builder.materialPath(
    fillAlpha: Float = 1f,
    strokeAlpha: Float = 1f,
    pathFillType: PathFillType = DefaultFillType,
    pathBuilder: PathBuilder.() -> Unit
) =
    path(
        fill = SolidColor(Color.Black),
        fillAlpha = fillAlpha,
        stroke = null,
        strokeAlpha = strokeAlpha,
        strokeLineWidth = 1f,
        strokeLineCap = StrokeCap.Butt,
        strokeLineJoin = StrokeJoin.Bevel,
        strokeLineMiter = 1f,
        pathFillType = pathFillType,
        pathBuilder = pathBuilder
    )