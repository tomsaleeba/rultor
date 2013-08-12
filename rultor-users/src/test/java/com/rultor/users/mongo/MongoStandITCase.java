/**
 * Copyright (c) 2009-2013, rultor.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the rultor.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.rultor.users.mongo;

import com.jcabi.aspects.Tv;
import com.rultor.spi.Pulse;
import com.rultor.spi.Stand;
import java.util.Iterator;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Integration case for {@link MongoStand}.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @checkstyle ClassDataAbstractionCoupling (500 lines)
 */
@SuppressWarnings({ "unchecked", "PMD.TooManyMethods" })
public final class MongoStandITCase {

    /**
     * Mongo host.
     */
    private static final String HOST =
        System.getProperty("failsafe.mongo.host");

    /**
     * Mongo port.
     */
    private static final String PORT =
        System.getProperty("failsafe.mongo.port");

    /**
     * Mongo database name.
     */
    private static final String NAME =
        System.getProperty("failsafe.mongo.name");

    /**
     * Mongo user.
     */
    private static final String USER =
        System.getProperty("failsafe.mongo.user");

    /**
     * Mongo password.
     */
    private static final String PWD =
        System.getProperty("failsafe.mongo.password");

    /**
     * MongoStand can accept and return pulses.
     * @throws Exception If some problem inside
     */
    @Test
    public void managesDataInMongoDb() throws Exception {
        final Stand stand = this.stand();
        final String pulse = RandomStringUtils.randomAlphabetic(Tv.TEN);
        stand.post(pulse, 2, "ADD 'tags'; ADD 'tag'; ADD 'label'; SET 'foo';");
        stand.post(pulse, 1, "ADD 'test'; SET 'hello, world!';");
        final Iterator<Pulse> pulses = stand.pulses().iterator();
        MatcherAssert.assertThat(pulses.hasNext(), Matchers.is(true));
        MatcherAssert.assertThat(
            pulses.next().xembly(),
            Matchers.allOf(
                Matchers.containsString("ADD 'test';"),
                Matchers.containsString("ADD 'tags';")
            )
        );
    }

    /**
     * Get stand to test against.
     * @return Stand to test
     * @throws Exception If some problem inside
     */
    private Stand stand() throws Exception {
        Assume.assumeNotNull(MongoStandITCase.HOST);
        final Stand origin = Mockito.mock(Stand.class);
        Mockito.doReturn(RandomStringUtils.randomAlphabetic(Tv.FIVE))
            .when(origin).name();
        return new MongoStand(
            new Mongo.Simple(
                MongoStandITCase.HOST,
                Integer.parseInt(MongoStandITCase.PORT),
                MongoStandITCase.NAME,
                MongoStandITCase.USER,
                MongoStandITCase.PWD
            ),
            origin
        );
    }

}
