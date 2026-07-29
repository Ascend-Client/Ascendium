package io.github.betterclient.ascendium.util

sealed interface Either<out L, out R> {
    data class Left<out L>(val value: L) : Either<L, Nothing>
    data class Right<out R>(val value: R) : Either<Nothing, R>

    companion object {
        fun <L> left(value: L): Either<L, Nothing> = Left(value)
        fun <R> right(value: R): Either<Nothing, R> = Right(value)
    }
}

inline fun <L, R, T> Either<L, R>.let(
    ifLeft: (L) -> T,
    ifRight: (R) -> T,
): T = when (this) {
    is Either.Left -> ifLeft(value)
    is Either.Right -> ifRight(value)
}