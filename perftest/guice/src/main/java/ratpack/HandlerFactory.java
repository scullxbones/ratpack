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

package ratpack;

import org.ratpackframework.guice.Guice;
import org.ratpackframework.handling.Chain;
import org.ratpackframework.handling.Handler;
import org.ratpackframework.launch.LaunchConfig;
import org.ratpackframework.util.Action;

public class HandlerFactory implements org.ratpackframework.launch.HandlerFactory {

  public Handler create(LaunchConfig launchConfig) {
    return Guice.handler(launchConfig, new ModuleBootstrap(), new Action<Chain>() {
      public void execute(Chain chain) {
        chain.add(chain.getRegistry().get(InjectedHandler.class));
      }
    });
  }

}
