package pt.isel.daw.gomoku.services.utils

data class PageResult<T>(
    val content: List<T>,
    val nextPage: Int?,
    val previousPage: Int?,
    val firstPage: Int?,
    val lastPage: Int?,
    val pageSize: Int,
) {
    companion object {
        private const val PAGE_SIZE = 20
        fun <T> toPage(objects: Collection<T>, page: Int): PageResult<T> {
            val from = (page - 1) * PAGE_SIZE
            var content: List<T> = emptyList()
            if (from < objects.size) {
                var to = Math.min(from + PAGE_SIZE, objects.size)
                to = if (to > objects.size) objects.size else to
                content = ArrayList(objects).subList(from, to)
            }
            val l = (objects.size - 1) / PAGE_SIZE + 1
            val lastPage = if (objects.isNotEmpty() && page < l) l else null
            val nextPage = if (lastPage != null && page < lastPage) page + 1 else null
            val previousPage = if (page > 1) page - 1 else null
            val firstPage = if (page > 1) 1 else null
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
