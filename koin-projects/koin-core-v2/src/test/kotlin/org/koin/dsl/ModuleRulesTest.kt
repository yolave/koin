package org.koin.dsl

import org.junit.Assert.fail
import org.junit.Test
import org.koin.Simple
import org.koin.core.error.AlreadyExistingDefinition
import org.koin.test.assertDefinitionsCount

class ModuleRulesTest {

    @Test
    fun `don't allow redeclaration`() {
        try {
            val app = koin {
                loadModules(module {
                    single { Simple.ComponentA() }
                    single { Simple.ComponentA() }
                })
            }
            fail("should not redeclare")
        } catch (e: AlreadyExistingDefinition) {
        }
    }

    @Test
    fun `allow redeclaration - different names`() {
        val app = koin {
            loadModules(module {
                single("default") { Simple.ComponentA() }
                single("other") { Simple.ComponentA() }
            })
        }
        app.assertDefinitionsCount(2)
    }

    @Test
    fun `allow redeclaration - default`() {
        val app = koin {
            loadModules(module {
                single { Simple.ComponentA() }
                single("other") { Simple.ComponentA() }
            })
        }
        app.assertDefinitionsCount(2)
    }
}