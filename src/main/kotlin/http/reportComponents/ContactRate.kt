package http.reportComponents

data class ContactRate(val totalPitches: Int, val ballsInPlay: Int) {
    val get = ballsInPlay.toDouble().div(totalPitches.toDouble()).times(100.toDouble())
    fun get() = ballsInPlay.toDouble().div(totalPitches.toDouble()).times(100.toDouble())
}