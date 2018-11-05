package org.koin.core.registry

import org.koin.core.bean.BeanDefinition
import org.koin.core.error.NoDefinitionFoundException
import java.util.*
import kotlin.reflect.KClass

class InstanceResolver {

    private val callStack = Stack<BeanDefinition<*>>()

    inline fun <reified T> resolveInstance(
        definition: BeanDefinition<*>?,
        clazz: KClass<*>
    ): T {
        checkForCycle(definition)

        addCallOnStack(definition)

        val instance = getInstance<T>(definition, clazz)

        checkCallIntegrity(definition)
        return instance
    }

    fun addCallOnStack(definition: BeanDefinition<*>?) {
        callStack.add(definition)
    }

    inline fun <reified T> getInstance(
        definition: BeanDefinition<*>?,
        clazz: KClass<*>
    ): T {
        return (definition?.instance?.get<T>()
                ?: throw NoDefinitionFoundException("No definition for '$clazz' has been found. Check your module definitions."))
    }

    fun checkCallIntegrity(definition: BeanDefinition<*>?) {
        val pop = callStack.pop()
        if (pop != definition) {
            error("CallStack integrity error while resolving $definition")
        }
    }

    fun checkForCycle(definition: BeanDefinition<*>?) {
        if (callStack.any { it == definition }) {
            error("CallStack cycle detected for $definition")
        }
    }

    fun close() {
        callStack.clear()
    }
}