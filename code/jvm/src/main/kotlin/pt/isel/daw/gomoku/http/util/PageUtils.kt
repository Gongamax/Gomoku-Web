package pt.isel.daw.gomoku.http.util

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

object PageUtils {
    fun <T> toPage(objects: Collection<T>, pageable: Pageable): Page<T> {
        val from = pageable.pageNumber * pageable.pageSize
        var content: List<T> = emptyList()
        if (from < objects.size) {
            var to = from + pageable.pageSize
            to = if (to > objects.size) objects.size else to
            content = ArrayList(objects).subList(from, to)
        }
        return PageImpl(content, pageable, objects.size.toLong())
    }
}