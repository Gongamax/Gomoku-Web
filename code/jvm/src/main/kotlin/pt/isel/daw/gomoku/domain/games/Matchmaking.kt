package pt.isel.daw.gomoku.domain.games

import pt.isel.daw.gomoku.domain.users.User
import pt.isel.daw.gomoku.utils.NodeLinkedList
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.time.Duration

typealias Match = Pair<User, User>

class Matchmaking<T> {

    private val lock: ReentrantLock = ReentrantLock()
    private val waiters: NodeLinkedList<Request> = NodeLinkedList()

    private class Request(
        val user: User,
        val condition: Condition,
        var opponent : User? = null,
    )

    @Throws(InterruptedException::class)
    fun waitForMatch(user: User, timeout: Duration): Match? {
        lock.withLock {
            //fast-path
            if (waiters.notEmpty) {
                val localRequest = waiters.pull().value
                val waitingUser = localRequest.user
                localRequest.opponent = user
                return Match(waitingUser, user)
            }
            // wait-path
            val localNode = waiters.enqueue(Request(user, lock.newCondition()))
            var remainingNanos = timeout.inWholeNanoseconds
            while (true) {
                try {
                    remainingNanos = localNode.value.condition.awaitNanos(remainingNanos)
                } catch (e: InterruptedException) {
                    val matchingUser = localNode.value.opponent
                    if (matchingUser != null) {
                        Thread.currentThread().interrupt()
                        return Match(user, matchingUser)
                    }
                    waiters.remove(localNode)
                    throw e
                }
                val matchingUser = localNode.value.opponent
                if (matchingUser != null) {
                    return Match(user, matchingUser)
                }
                if (remainingNanos <= 0) {
                    waiters.remove(localNode)
                    return null
                }
            }
        }
    }

    @Throws(InterruptedException::class)
    fun quitWaiting(user: User) : Boolean {
        lock.withLock {
            var success : Boolean = false
            waiters.forEach { request ->
                if (request.user == user) {
                    waiters.dequeue(request)
                    success = true
                }
            }
            return success
        }
    }
}