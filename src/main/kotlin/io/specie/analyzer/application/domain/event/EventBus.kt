package io.specie.analyzer.application.domain.event

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class EventBus : EventNotifier {

    private val sharedFlow = MutableSharedFlow<Event>()

    val events = sharedFlow.asSharedFlow()
    val scope = CoroutineScope(Dispatchers.IO + CoroutineName("eventbus") + SupervisorJob())

    override fun notify(event: Event) {
        scope.launch { sharedFlow.emit(event) }
    }

    inline fun <reified T : Event> register(listener: EventListener<T>): EventBus {
        scope.launch { events.map { it as T }.collect { listener.onEvent(it) } }
        return this
    }
}
