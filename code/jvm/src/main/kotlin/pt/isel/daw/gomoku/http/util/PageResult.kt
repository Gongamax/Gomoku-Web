package pt.isel.daw.gomoku.http.util

/*TODO(CREATE FUNCTION THAT TURNS SEARCH INTO PAGE)*/
data class PageResult<T>(
    val content: List<T>,
    val nextPage: Int?,
    val previousPage: Int?,
    val firstPage: Int?,
    val lastPage: Int,
    val pageSize: Int,
) {
    companion object {
        private const val PAGE_SIZE = 20
        fun <T> toPage(objects: Collection<T>, page: Int): PageResult<T> {
            val from = page * PAGE_SIZE
            var content: List<T> = emptyList()
            if (from < objects.size) {
                var to = from + PAGE_SIZE
                to = if (to > objects.size) objects.size else to
                content = ArrayList(objects).subList(from, to)
            }
            val lastPage = (objects.size / PAGE_SIZE.toFloat()).toInt()
            val nextPage = if (page < lastPage) page + 1 else null
            val previousPage = if (page > 0) page - 1 else null
            val firstPage = if (page > 0) 0 else null
            return PageResult(
                content,
                nextPage,
                previousPage,
                firstPage,
                lastPage,
                PAGE_SIZE
            )
        }
    }
}
