/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ratpackframework.guice

import com.google.inject.AbstractModule
import org.ratpackframework.handling.Context
import org.ratpackframework.handling.Handler
import org.ratpackframework.test.groovy.RatpackGroovyDslSpec

import javax.inject.Inject

class InjectedHandlersSpec extends RatpackGroovyDslSpec {

  static class Injectable {
    String name
  }

  static class InjectedHandler implements Handler {

    Injectable injectable

    @Inject
    InjectedHandler(Injectable injectable) {
      this.injectable = injectable
    }

    @Override
    void handle(Context exchange) {
      exchange.response.send(injectable.name)
    }
  }

  def "can use injected handlers"() {
    given:
    def nameValue = "foo"

    when:
    app {
      modules {

        assert launchConfig != null

        register(new AbstractModule() {
          @Override
          protected void configure() {
            bind(Injectable).toInstance(new Injectable(name: nameValue))
          }
        })
      }

      handlers {
        add registry.get(InjectedHandler)
      }
    }

    then:
    text == nameValue
  }

}
