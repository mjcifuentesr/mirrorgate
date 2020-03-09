/*
 * Copyright 2017 Banco Bilbao Vizcaya Argentaria, S.A.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

genericTileComponentTest('simple-program-increment','.indicator');

describe('<simple-program-increment>', function() {

  let server;

  beforeEach(function() {
    server = buildFakeServer();
  });

  afterEach(function() {
    server.restore();
  });

  it('should give Program Increment stats', function(done) {
    createTestComponent('simple-program-increment').then(function (component) {
      server.respond();

      setTimeout(function () {
        let pi = component.getModel().programIncrement;
        expect(pi.stats).not.toBe(undefined);
        done();
      });

    });
  });
});