/**
 * Copyright (c) 2009-2014, rultor.com
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
package com.rultor.profiles;

import com.jcabi.aspects.Immutable;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.util.List;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directive;
import org.xembly.Directives;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;
import org.yaml.snakeyaml.Yaml;

/**
 * YAML into XML.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 1.0
 */
@Immutable
@ToString
@EqualsAndHashCode(of = "yaml")
final class YamlXML {

    /**
     * Yaml.
     */
    private final transient String yaml;

    /**
     * Ctor.
     * @param yml YAML
     */
    YamlXML(final String yml) {
        this.yaml = yml;
    }

    /**
     * Get XML.
     * @return XML
     */
    public XML get() {
        final Yaml parser = new Yaml();
        final Directives dirs = new Directives().add("p").append(
            YamlXML.dirs(parser.load(this.yaml))
        );
        try {
            return new XMLDocument(new Xembler(dirs).xml());
        } catch (final ImpossibleModificationException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Convert something to dirs.
     * @param obj Object
     * @return Dirs
     */
    private static Iterable<Directive> dirs(final Object obj) {
        final Directives dirs = new Directives();
        if (obj instanceof Map) {
            for (final Map.Entry<String, Object> ent
                : ((Map<String, Object>) obj).entrySet()) {
                dirs.add(ent.getKey())
                    .append(YamlXML.dirs(ent.getValue()))
                    .up();
            }
        } else if (obj instanceof List) {
            int idx = 0;
            for (final Object item : (List<Object>) obj) {
                dirs.add("item")
                    .attr("idx", Integer.toString(idx))
                    .append(YamlXML.dirs(item))
                    .up();
            }
        } else {
            dirs.set(obj.toString());
        }
        return dirs;
    }

}
