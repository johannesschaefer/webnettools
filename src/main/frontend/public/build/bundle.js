
(function(l, r) { if (l.getElementById('livereloadscript')) return; r = l.createElement('script'); r.async = 1; r.src = '//' + (window.location.host || 'localhost').split(':')[0] + ':35729/livereload.js?snipver=1'; r.id = 'livereloadscript'; l.getElementsByTagName('head')[0].appendChild(r) })(window.document);
var app = (function () {
    'use strict';

    function noop() { }
    function add_location(element, file, line, column, char) {
        element.__svelte_meta = {
            loc: { file, line, column, char }
        };
    }
    function run(fn) {
        return fn();
    }
    function blank_object() {
        return Object.create(null);
    }
    function run_all(fns) {
        fns.forEach(run);
    }
    function is_function(thing) {
        return typeof thing === 'function';
    }
    function safe_not_equal(a, b) {
        return a != a ? b == b : a !== b || ((a && typeof a === 'object') || typeof a === 'function');
    }
    function is_empty(obj) {
        return Object.keys(obj).length === 0;
    }

    function append(target, node) {
        target.appendChild(node);
    }
    function insert(target, node, anchor) {
        target.insertBefore(node, anchor || null);
    }
    function detach(node) {
        node.parentNode.removeChild(node);
    }
    function element(name) {
        return document.createElement(name);
    }
    function text(data) {
        return document.createTextNode(data);
    }
    function space() {
        return text(' ');
    }
    function listen(node, event, handler, options) {
        node.addEventListener(event, handler, options);
        return () => node.removeEventListener(event, handler, options);
    }
    function attr(node, attribute, value) {
        if (value == null)
            node.removeAttribute(attribute);
        else if (node.getAttribute(attribute) !== value)
            node.setAttribute(attribute, value);
    }
    function children(element) {
        return Array.from(element.childNodes);
    }
    function set_input_value(input, value) {
        input.value = value == null ? '' : value;
    }
    function set_style(node, key, value, important) {
        node.style.setProperty(key, value, important ? 'important' : '');
    }
    function custom_event(type, detail) {
        const e = document.createEvent('CustomEvent');
        e.initCustomEvent(type, false, false, detail);
        return e;
    }

    let current_component;
    function set_current_component(component) {
        current_component = component;
    }

    const dirty_components = [];
    const binding_callbacks = [];
    const render_callbacks = [];
    const flush_callbacks = [];
    const resolved_promise = Promise.resolve();
    let update_scheduled = false;
    function schedule_update() {
        if (!update_scheduled) {
            update_scheduled = true;
            resolved_promise.then(flush);
        }
    }
    function add_render_callback(fn) {
        render_callbacks.push(fn);
    }
    let flushing = false;
    const seen_callbacks = new Set();
    function flush() {
        if (flushing)
            return;
        flushing = true;
        do {
            // first, call beforeUpdate functions
            // and update components
            for (let i = 0; i < dirty_components.length; i += 1) {
                const component = dirty_components[i];
                set_current_component(component);
                update(component.$$);
            }
            set_current_component(null);
            dirty_components.length = 0;
            while (binding_callbacks.length)
                binding_callbacks.pop()();
            // then, once components are updated, call
            // afterUpdate functions. This may cause
            // subsequent updates...
            for (let i = 0; i < render_callbacks.length; i += 1) {
                const callback = render_callbacks[i];
                if (!seen_callbacks.has(callback)) {
                    // ...so guard against infinite loops
                    seen_callbacks.add(callback);
                    callback();
                }
            }
            render_callbacks.length = 0;
        } while (dirty_components.length);
        while (flush_callbacks.length) {
            flush_callbacks.pop()();
        }
        update_scheduled = false;
        flushing = false;
        seen_callbacks.clear();
    }
    function update($$) {
        if ($$.fragment !== null) {
            $$.update();
            run_all($$.before_update);
            const dirty = $$.dirty;
            $$.dirty = [-1];
            $$.fragment && $$.fragment.p($$.ctx, dirty);
            $$.after_update.forEach(add_render_callback);
        }
    }
    const outroing = new Set();
    let outros;
    function transition_in(block, local) {
        if (block && block.i) {
            outroing.delete(block);
            block.i(local);
        }
    }
    function transition_out(block, local, detach, callback) {
        if (block && block.o) {
            if (outroing.has(block))
                return;
            outroing.add(block);
            outros.c.push(() => {
                outroing.delete(block);
                if (callback) {
                    if (detach)
                        block.d(1);
                    callback();
                }
            });
            block.o(local);
        }
    }

    const globals = (typeof window !== 'undefined'
        ? window
        : typeof globalThis !== 'undefined'
            ? globalThis
            : global);
    function create_component(block) {
        block && block.c();
    }
    function mount_component(component, target, anchor) {
        const { fragment, on_mount, on_destroy, after_update } = component.$$;
        fragment && fragment.m(target, anchor);
        // onMount happens before the initial afterUpdate
        add_render_callback(() => {
            const new_on_destroy = on_mount.map(run).filter(is_function);
            if (on_destroy) {
                on_destroy.push(...new_on_destroy);
            }
            else {
                // Edge case - component was destroyed immediately,
                // most likely as a result of a binding initialising
                run_all(new_on_destroy);
            }
            component.$$.on_mount = [];
        });
        after_update.forEach(add_render_callback);
    }
    function destroy_component(component, detaching) {
        const $$ = component.$$;
        if ($$.fragment !== null) {
            run_all($$.on_destroy);
            $$.fragment && $$.fragment.d(detaching);
            // TODO null out other refs, including component.$$ (but need to
            // preserve final state?)
            $$.on_destroy = $$.fragment = null;
            $$.ctx = [];
        }
    }
    function make_dirty(component, i) {
        if (component.$$.dirty[0] === -1) {
            dirty_components.push(component);
            schedule_update();
            component.$$.dirty.fill(0);
        }
        component.$$.dirty[(i / 31) | 0] |= (1 << (i % 31));
    }
    function init(component, options, instance, create_fragment, not_equal, props, dirty = [-1]) {
        const parent_component = current_component;
        set_current_component(component);
        const prop_values = options.props || {};
        const $$ = component.$$ = {
            fragment: null,
            ctx: null,
            // state
            props,
            update: noop,
            not_equal,
            bound: blank_object(),
            // lifecycle
            on_mount: [],
            on_destroy: [],
            before_update: [],
            after_update: [],
            context: new Map(parent_component ? parent_component.$$.context : []),
            // everything else
            callbacks: blank_object(),
            dirty,
            skip_bound: false
        };
        let ready = false;
        $$.ctx = instance
            ? instance(component, prop_values, (i, ret, ...rest) => {
                const value = rest.length ? rest[0] : ret;
                if ($$.ctx && not_equal($$.ctx[i], $$.ctx[i] = value)) {
                    if (!$$.skip_bound && $$.bound[i])
                        $$.bound[i](value);
                    if (ready)
                        make_dirty(component, i);
                }
                return ret;
            })
            : [];
        $$.update();
        ready = true;
        run_all($$.before_update);
        // `false` as a special case of no DOM component
        $$.fragment = create_fragment ? create_fragment($$.ctx) : false;
        if (options.target) {
            if (options.hydrate) {
                const nodes = children(options.target);
                // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
                $$.fragment && $$.fragment.l(nodes);
                nodes.forEach(detach);
            }
            else {
                // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
                $$.fragment && $$.fragment.c();
            }
            if (options.intro)
                transition_in(component.$$.fragment);
            mount_component(component, options.target, options.anchor);
            flush();
        }
        set_current_component(parent_component);
    }
    /**
     * Base class for Svelte components. Used when dev=false.
     */
    class SvelteComponent {
        $destroy() {
            destroy_component(this, 1);
            this.$destroy = noop;
        }
        $on(type, callback) {
            const callbacks = (this.$$.callbacks[type] || (this.$$.callbacks[type] = []));
            callbacks.push(callback);
            return () => {
                const index = callbacks.indexOf(callback);
                if (index !== -1)
                    callbacks.splice(index, 1);
            };
        }
        $set($$props) {
            if (this.$$set && !is_empty($$props)) {
                this.$$.skip_bound = true;
                this.$$set($$props);
                this.$$.skip_bound = false;
            }
        }
    }

    function dispatch_dev(type, detail) {
        document.dispatchEvent(custom_event(type, Object.assign({ version: '3.31.2' }, detail)));
    }
    function append_dev(target, node) {
        dispatch_dev('SvelteDOMInsert', { target, node });
        append(target, node);
    }
    function insert_dev(target, node, anchor) {
        dispatch_dev('SvelteDOMInsert', { target, node, anchor });
        insert(target, node, anchor);
    }
    function detach_dev(node) {
        dispatch_dev('SvelteDOMRemove', { node });
        detach(node);
    }
    function listen_dev(node, event, handler, options, has_prevent_default, has_stop_propagation) {
        const modifiers = options === true ? ['capture'] : options ? Array.from(Object.keys(options)) : [];
        if (has_prevent_default)
            modifiers.push('preventDefault');
        if (has_stop_propagation)
            modifiers.push('stopPropagation');
        dispatch_dev('SvelteDOMAddEventListener', { node, event, handler, modifiers });
        const dispose = listen(node, event, handler, options);
        return () => {
            dispatch_dev('SvelteDOMRemoveEventListener', { node, event, handler, modifiers });
            dispose();
        };
    }
    function attr_dev(node, attribute, value) {
        attr(node, attribute, value);
        if (value == null)
            dispatch_dev('SvelteDOMRemoveAttribute', { node, attribute });
        else
            dispatch_dev('SvelteDOMSetAttribute', { node, attribute, value });
    }
    function validate_slots(name, slot, keys) {
        for (const slot_key of Object.keys(slot)) {
            if (!~keys.indexOf(slot_key)) {
                console.warn(`<${name}> received an unexpected slot "${slot_key}".`);
            }
        }
    }
    /**
     * Base class for Svelte components with some minor dev-enhancements. Used when dev=true.
     */
    class SvelteComponentDev extends SvelteComponent {
        constructor(options) {
            if (!options || (!options.target && !options.$$inline)) {
                throw new Error("'target' is a required option");
            }
            super();
        }
        $destroy() {
            super.$destroy();
            this.$destroy = () => {
                console.warn('Component was already destroyed'); // eslint-disable-line no-console
            };
        }
        $capture_state() { }
        $inject_state() { }
    }

    var commonjsGlobal = typeof globalThis !== 'undefined' ? globalThis : typeof window !== 'undefined' ? window : typeof global !== 'undefined' ? global : typeof self !== 'undefined' ? self : {};

    function getDefaultExportFromCjs (x) {
    	return x && x.__esModule && Object.prototype.hasOwnProperty.call(x, 'default') ? x['default'] : x;
    }

    function createCommonjsModule(fn, basedir, module) {
    	return module = {
    		path: basedir,
    		exports: {},
    		require: function (path, base) {
    			return commonjsRequire(path, (base === undefined || base === null) ? module.path : base);
    		}
    	}, fn(module, module.exports), module.exports;
    }

    function commonjsRequire () {
    	throw new Error('Dynamic requires are not currently supported by @rollup/plugin-commonjs');
    }

    var ansi_up = createCommonjsModule(function (module, exports) {
    /*  ansi_up.js
     *  author : Dru Nelson
     *  license : MIT
     *  http://github.com/drudru/ansi_up
     */
    (function (root, factory) {
        if ( typeof exports.nodeName !== 'string') {
            // CommonJS
            factory(exports);
        } else {
            // Browser globals
            var exp = {};
            factory(exp);
            root.AnsiUp = exp.default;
        }
    }(commonjsGlobal, function (exports) {
    var __makeTemplateObject = (this && this.__makeTemplateObject) || function (cooked, raw) {
        if (Object.defineProperty) { Object.defineProperty(cooked, "raw", { value: raw }); } else { cooked.raw = raw; }
        return cooked;
    };
    var PacketKind;
    (function (PacketKind) {
        PacketKind[PacketKind["EOS"] = 0] = "EOS";
        PacketKind[PacketKind["Text"] = 1] = "Text";
        PacketKind[PacketKind["Incomplete"] = 2] = "Incomplete";
        PacketKind[PacketKind["ESC"] = 3] = "ESC";
        PacketKind[PacketKind["Unknown"] = 4] = "Unknown";
        PacketKind[PacketKind["SGR"] = 5] = "SGR";
        PacketKind[PacketKind["OSCURL"] = 6] = "OSCURL";
    })(PacketKind || (PacketKind = {}));
    var AnsiUp = (function () {
        function AnsiUp() {
            this.VERSION = "4.0.4";
            this.setup_palettes();
            this._use_classes = false;
            this._escape_for_html = true;
            this.bold = false;
            this.fg = this.bg = null;
            this._buffer = '';
            this._url_whitelist = { 'http': 1, 'https': 1 };
        }
        Object.defineProperty(AnsiUp.prototype, "use_classes", {
            get: function () {
                return this._use_classes;
            },
            set: function (arg) {
                this._use_classes = arg;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(AnsiUp.prototype, "escape_for_html", {
            get: function () {
                return this._escape_for_html;
            },
            set: function (arg) {
                this._escape_for_html = arg;
            },
            enumerable: true,
            configurable: true
        });
        Object.defineProperty(AnsiUp.prototype, "url_whitelist", {
            get: function () {
                return this._url_whitelist;
            },
            set: function (arg) {
                this._url_whitelist = arg;
            },
            enumerable: true,
            configurable: true
        });
        AnsiUp.prototype.setup_palettes = function () {
            var _this = this;
            this.ansi_colors =
                [
                    [
                        { rgb: [0, 0, 0], class_name: "ansi-black" },
                        { rgb: [187, 0, 0], class_name: "ansi-red" },
                        { rgb: [0, 187, 0], class_name: "ansi-green" },
                        { rgb: [187, 187, 0], class_name: "ansi-yellow" },
                        { rgb: [0, 0, 187], class_name: "ansi-blue" },
                        { rgb: [187, 0, 187], class_name: "ansi-magenta" },
                        { rgb: [0, 187, 187], class_name: "ansi-cyan" },
                        { rgb: [255, 255, 255], class_name: "ansi-white" }
                    ],
                    [
                        { rgb: [85, 85, 85], class_name: "ansi-bright-black" },
                        { rgb: [255, 85, 85], class_name: "ansi-bright-red" },
                        { rgb: [0, 255, 0], class_name: "ansi-bright-green" },
                        { rgb: [255, 255, 85], class_name: "ansi-bright-yellow" },
                        { rgb: [85, 85, 255], class_name: "ansi-bright-blue" },
                        { rgb: [255, 85, 255], class_name: "ansi-bright-magenta" },
                        { rgb: [85, 255, 255], class_name: "ansi-bright-cyan" },
                        { rgb: [255, 255, 255], class_name: "ansi-bright-white" }
                    ]
                ];
            this.palette_256 = [];
            this.ansi_colors.forEach(function (palette) {
                palette.forEach(function (rec) {
                    _this.palette_256.push(rec);
                });
            });
            var levels = [0, 95, 135, 175, 215, 255];
            for (var r = 0; r < 6; ++r) {
                for (var g = 0; g < 6; ++g) {
                    for (var b = 0; b < 6; ++b) {
                        var col = { rgb: [levels[r], levels[g], levels[b]], class_name: 'truecolor' };
                        this.palette_256.push(col);
                    }
                }
            }
            var grey_level = 8;
            for (var i = 0; i < 24; ++i, grey_level += 10) {
                var gry = { rgb: [grey_level, grey_level, grey_level], class_name: 'truecolor' };
                this.palette_256.push(gry);
            }
        };
        AnsiUp.prototype.escape_txt_for_html = function (txt) {
            return txt.replace(/[&<>]/gm, function (str) {
                if (str === "&")
                    return "&amp;";
                if (str === "<")
                    return "&lt;";
                if (str === ">")
                    return "&gt;";
            });
        };
        AnsiUp.prototype.append_buffer = function (txt) {
            var str = this._buffer + txt;
            this._buffer = str;
        };
        AnsiUp.prototype.get_next_packet = function () {
            var pkt = {
                kind: PacketKind.EOS,
                text: '',
                url: ''
            };
            var len = this._buffer.length;
            if (len == 0)
                return pkt;
            var pos = this._buffer.indexOf("\x1B");
            if (pos == -1) {
                pkt.kind = PacketKind.Text;
                pkt.text = this._buffer;
                this._buffer = '';
                return pkt;
            }
            if (pos > 0) {
                pkt.kind = PacketKind.Text;
                pkt.text = this._buffer.slice(0, pos);
                this._buffer = this._buffer.slice(pos);
                return pkt;
            }
            if (pos == 0) {
                if (len == 1) {
                    pkt.kind = PacketKind.Incomplete;
                    return pkt;
                }
                var next_char = this._buffer.charAt(1);
                if ((next_char != '[') && (next_char != ']')) {
                    pkt.kind = PacketKind.ESC;
                    pkt.text = this._buffer.slice(0, 1);
                    this._buffer = this._buffer.slice(1);
                    return pkt;
                }
                if (next_char == '[') {
                    if (!this._csi_regex) {
                        this._csi_regex = rgx(__makeTemplateObject(["\n                        ^                           # beginning of line\n                                                    #\n                                                    # First attempt\n                        (?:                         # legal sequence\n                          \u001B[                      # CSI\n                          ([<-?]?)              # private-mode char\n                          ([d;]*)                    # any digits or semicolons\n                          ([ -/]?               # an intermediate modifier\n                          [@-~])                # the command\n                        )\n                        |                           # alternate (second attempt)\n                        (?:                         # illegal sequence\n                          \u001B[                      # CSI\n                          [ -~]*                # anything legal\n                          ([\0-\u001F:])              # anything illegal\n                        )\n                    "], ["\n                        ^                           # beginning of line\n                                                    #\n                                                    # First attempt\n                        (?:                         # legal sequence\n                          \\x1b\\[                      # CSI\n                          ([\\x3c-\\x3f]?)              # private-mode char\n                          ([\\d;]*)                    # any digits or semicolons\n                          ([\\x20-\\x2f]?               # an intermediate modifier\n                          [\\x40-\\x7e])                # the command\n                        )\n                        |                           # alternate (second attempt)\n                        (?:                         # illegal sequence\n                          \\x1b\\[                      # CSI\n                          [\\x20-\\x7e]*                # anything legal\n                          ([\\x00-\\x1f:])              # anything illegal\n                        )\n                    "]));
                    }
                    var match = this._buffer.match(this._csi_regex);
                    if (match === null) {
                        pkt.kind = PacketKind.Incomplete;
                        return pkt;
                    }
                    if (match[4]) {
                        pkt.kind = PacketKind.ESC;
                        pkt.text = this._buffer.slice(0, 1);
                        this._buffer = this._buffer.slice(1);
                        return pkt;
                    }
                    if ((match[1] != '') || (match[3] != 'm'))
                        pkt.kind = PacketKind.Unknown;
                    else
                        pkt.kind = PacketKind.SGR;
                    pkt.text = match[2];
                    var rpos = match[0].length;
                    this._buffer = this._buffer.slice(rpos);
                    return pkt;
                }
                if (next_char == ']') {
                    if (len < 4) {
                        pkt.kind = PacketKind.Incomplete;
                        return pkt;
                    }
                    if ((this._buffer.charAt(2) != '8')
                        || (this._buffer.charAt(3) != ';')) {
                        pkt.kind = PacketKind.ESC;
                        pkt.text = this._buffer.slice(0, 1);
                        this._buffer = this._buffer.slice(1);
                        return pkt;
                    }
                    if (!this._osc_st) {
                        this._osc_st = rgxG(__makeTemplateObject(["\n                        (?:                         # legal sequence\n                          (\u001B\\)                    # ESC                           |                           # alternate\n                          (\u0007)                      # BEL (what xterm did)\n                        )\n                        |                           # alternate (second attempt)\n                        (                           # illegal sequence\n                          [\0-\u0006]                 # anything illegal\n                          |                           # alternate\n                          [\b-\u001A]                 # anything illegal\n                          |                           # alternate\n                          [\u001C-\u001F]                 # anything illegal\n                        )\n                    "], ["\n                        (?:                         # legal sequence\n                          (\\x1b\\\\)                    # ESC \\\n                          |                           # alternate\n                          (\\x07)                      # BEL (what xterm did)\n                        )\n                        |                           # alternate (second attempt)\n                        (                           # illegal sequence\n                          [\\x00-\\x06]                 # anything illegal\n                          |                           # alternate\n                          [\\x08-\\x1a]                 # anything illegal\n                          |                           # alternate\n                          [\\x1c-\\x1f]                 # anything illegal\n                        )\n                    "]));
                    }
                    this._osc_st.lastIndex = 0;
                    {
                        var match_1 = this._osc_st.exec(this._buffer);
                        if (match_1 === null) {
                            pkt.kind = PacketKind.Incomplete;
                            return pkt;
                        }
                        if (match_1[3]) {
                            pkt.kind = PacketKind.ESC;
                            pkt.text = this._buffer.slice(0, 1);
                            this._buffer = this._buffer.slice(1);
                            return pkt;
                        }
                    }
                    {
                        var match_2 = this._osc_st.exec(this._buffer);
                        if (match_2 === null) {
                            pkt.kind = PacketKind.Incomplete;
                            return pkt;
                        }
                        if (match_2[3]) {
                            pkt.kind = PacketKind.ESC;
                            pkt.text = this._buffer.slice(0, 1);
                            this._buffer = this._buffer.slice(1);
                            return pkt;
                        }
                    }
                    if (!this._osc_regex) {
                        this._osc_regex = rgx(__makeTemplateObject(["\n                        ^                           # beginning of line\n                                                    #\n                        \u001B]8;                    # OSC Hyperlink\n                        [ -:<-~]*       # params (excluding ;)\n                        ;                           # end of params\n                        ([!-~]{0,512})        # URL capture\n                        (?:                         # ST\n                          (?:\u001B\\)                  # ESC                           |                           # alternate\n                          (?:\u0007)                    # BEL (what xterm did)\n                        )\n                        ([!-~]+)              # TEXT capture\n                        \u001B]8;;                   # OSC Hyperlink End\n                        (?:                         # ST\n                          (?:\u001B\\)                  # ESC                           |                           # alternate\n                          (?:\u0007)                    # BEL (what xterm did)\n                        )\n                    "], ["\n                        ^                           # beginning of line\n                                                    #\n                        \\x1b\\]8;                    # OSC Hyperlink\n                        [\\x20-\\x3a\\x3c-\\x7e]*       # params (excluding ;)\n                        ;                           # end of params\n                        ([\\x21-\\x7e]{0,512})        # URL capture\n                        (?:                         # ST\n                          (?:\\x1b\\\\)                  # ESC \\\n                          |                           # alternate\n                          (?:\\x07)                    # BEL (what xterm did)\n                        )\n                        ([\\x21-\\x7e]+)              # TEXT capture\n                        \\x1b\\]8;;                   # OSC Hyperlink End\n                        (?:                         # ST\n                          (?:\\x1b\\\\)                  # ESC \\\n                          |                           # alternate\n                          (?:\\x07)                    # BEL (what xterm did)\n                        )\n                    "]));
                    }
                    var match = this._buffer.match(this._osc_regex);
                    if (match === null) {
                        pkt.kind = PacketKind.ESC;
                        pkt.text = this._buffer.slice(0, 1);
                        this._buffer = this._buffer.slice(1);
                        return pkt;
                    }
                    pkt.kind = PacketKind.OSCURL;
                    pkt.url = match[1];
                    pkt.text = match[2];
                    var rpos = match[0].length;
                    this._buffer = this._buffer.slice(rpos);
                    return pkt;
                }
            }
        };
        AnsiUp.prototype.ansi_to_html = function (txt) {
            this.append_buffer(txt);
            var blocks = [];
            while (true) {
                var packet = this.get_next_packet();
                if ((packet.kind == PacketKind.EOS)
                    || (packet.kind == PacketKind.Incomplete))
                    break;
                if ((packet.kind == PacketKind.ESC)
                    || (packet.kind == PacketKind.Unknown))
                    continue;
                if (packet.kind == PacketKind.Text)
                    blocks.push(this.transform_to_html(this.with_state(packet)));
                else if (packet.kind == PacketKind.SGR)
                    this.process_ansi(packet);
                else if (packet.kind == PacketKind.OSCURL)
                    blocks.push(this.process_hyperlink(packet));
            }
            return blocks.join("");
        };
        AnsiUp.prototype.with_state = function (pkt) {
            return { bold: this.bold, fg: this.fg, bg: this.bg, text: pkt.text };
        };
        AnsiUp.prototype.process_ansi = function (pkt) {
            var sgr_cmds = pkt.text.split(';');
            while (sgr_cmds.length > 0) {
                var sgr_cmd_str = sgr_cmds.shift();
                var num = parseInt(sgr_cmd_str, 10);
                if (isNaN(num) || num === 0) {
                    this.fg = this.bg = null;
                    this.bold = false;
                }
                else if (num === 1) {
                    this.bold = true;
                }
                else if (num === 22) {
                    this.bold = false;
                }
                else if (num === 39) {
                    this.fg = null;
                }
                else if (num === 49) {
                    this.bg = null;
                }
                else if ((num >= 30) && (num < 38)) {
                    this.fg = this.ansi_colors[0][(num - 30)];
                }
                else if ((num >= 40) && (num < 48)) {
                    this.bg = this.ansi_colors[0][(num - 40)];
                }
                else if ((num >= 90) && (num < 98)) {
                    this.fg = this.ansi_colors[1][(num - 90)];
                }
                else if ((num >= 100) && (num < 108)) {
                    this.bg = this.ansi_colors[1][(num - 100)];
                }
                else if (num === 38 || num === 48) {
                    if (sgr_cmds.length > 0) {
                        var is_foreground = (num === 38);
                        var mode_cmd = sgr_cmds.shift();
                        if (mode_cmd === '5' && sgr_cmds.length > 0) {
                            var palette_index = parseInt(sgr_cmds.shift(), 10);
                            if (palette_index >= 0 && palette_index <= 255) {
                                if (is_foreground)
                                    this.fg = this.palette_256[palette_index];
                                else
                                    this.bg = this.palette_256[palette_index];
                            }
                        }
                        if (mode_cmd === '2' && sgr_cmds.length > 2) {
                            var r = parseInt(sgr_cmds.shift(), 10);
                            var g = parseInt(sgr_cmds.shift(), 10);
                            var b = parseInt(sgr_cmds.shift(), 10);
                            if ((r >= 0 && r <= 255) && (g >= 0 && g <= 255) && (b >= 0 && b <= 255)) {
                                var c = { rgb: [r, g, b], class_name: 'truecolor' };
                                if (is_foreground)
                                    this.fg = c;
                                else
                                    this.bg = c;
                            }
                        }
                    }
                }
            }
        };
        AnsiUp.prototype.transform_to_html = function (fragment) {
            var txt = fragment.text;
            if (txt.length === 0)
                return txt;
            if (this._escape_for_html)
                txt = this.escape_txt_for_html(txt);
            if (!fragment.bold && fragment.fg === null && fragment.bg === null)
                return txt;
            var styles = [];
            var classes = [];
            var fg = fragment.fg;
            var bg = fragment.bg;
            if (fragment.bold)
                styles.push('font-weight:bold');
            if (!this._use_classes) {
                if (fg)
                    styles.push("color:rgb(" + fg.rgb.join(',') + ")");
                if (bg)
                    styles.push("background-color:rgb(" + bg.rgb + ")");
            }
            else {
                if (fg) {
                    if (fg.class_name !== 'truecolor') {
                        classes.push(fg.class_name + "-fg");
                    }
                    else {
                        styles.push("color:rgb(" + fg.rgb.join(',') + ")");
                    }
                }
                if (bg) {
                    if (bg.class_name !== 'truecolor') {
                        classes.push(bg.class_name + "-bg");
                    }
                    else {
                        styles.push("background-color:rgb(" + bg.rgb.join(',') + ")");
                    }
                }
            }
            var class_string = '';
            var style_string = '';
            if (classes.length)
                class_string = " class=\"" + classes.join(' ') + "\"";
            if (styles.length)
                style_string = " style=\"" + styles.join(';') + "\"";
            return "<span" + style_string + class_string + ">" + txt + "</span>";
        };
        AnsiUp.prototype.process_hyperlink = function (pkt) {
            var parts = pkt.url.split(':');
            if (parts.length < 1)
                return '';
            if (!this._url_whitelist[parts[0]])
                return '';
            var result = "<a href=\"" + this.escape_txt_for_html(pkt.url) + "\">" + this.escape_txt_for_html(pkt.text) + "</a>";
            return result;
        };
        return AnsiUp;
    }());
    function rgx(tmplObj) {
        var subst = [];
        for (var _i = 1; _i < arguments.length; _i++) {
            subst[_i - 1] = arguments[_i];
        }
        var regexText = tmplObj.raw[0];
        var wsrgx = /^\s+|\s+\n|\s*#[\s\S]*?\n|\n/gm;
        var txt2 = regexText.replace(wsrgx, '');
        return new RegExp(txt2);
    }
    function rgxG(tmplObj) {
        var subst = [];
        for (var _i = 1; _i < arguments.length; _i++) {
            subst[_i - 1] = arguments[_i];
        }
        var regexText = tmplObj.raw[0];
        var wsrgx = /^\s+|\s+\n|\s*#[\s\S]*?\n|\n/gm;
        var txt2 = regexText.replace(wsrgx, '');
        return new RegExp(txt2, 'g');
    }

        Object.defineProperty(exports, "__esModule", { value: true });
        exports.default = AnsiUp;
    }));
    });

    var AnsiUp = /*@__PURE__*/getDefaultExportFromCjs(ansi_up);

    /* src/Result.svelte generated by Svelte v3.31.2 */

    const { console: console_1 } = globals;
    const file = "src/Result.svelte";

    function create_fragment(ctx) {
    	let div15;
    	let div14;
    	let div13;
    	let div12;
    	let div3;
    	let div0;
    	let h20;
    	let button0;
    	let t1;
    	let div2;
    	let div1;
    	let t2;
    	let code;
    	let t4;
    	let t5;
    	let div7;
    	let div4;
    	let h21;
    	let button1;
    	let t7;
    	let div6;
    	let div5;
    	let t9;
    	let div11;
    	let div8;
    	let h22;
    	let button2;
    	let t11;
    	let div10;
    	let div9;
    	let t13;
    	let style;

    	const block = {
    		c: function create() {
    			div15 = element("div");
    			div14 = element("div");
    			div13 = element("div");
    			div12 = element("div");
    			div3 = element("div");
    			div0 = element("div");
    			h20 = element("h2");
    			button0 = element("button");
    			button0.textContent = "Collapsible Group Item #1";
    			t1 = space();
    			div2 = element("div");
    			div1 = element("div");
    			t2 = text("Some placeholder content for the first accordion\n                            panel. This panel is shown by default, thanks to the ");
    			code = element("code");
    			code.textContent = ".show";
    			t4 = text(" class.");
    			t5 = space();
    			div7 = element("div");
    			div4 = element("div");
    			h21 = element("h2");
    			button1 = element("button");
    			button1.textContent = "Collapsible Group Item #2";
    			t7 = space();
    			div6 = element("div");
    			div5 = element("div");
    			div5.textContent = "Some placeholder content for the second accordion\n                            panel. This panel is hidden by default.";
    			t9 = space();
    			div11 = element("div");
    			div8 = element("div");
    			h22 = element("h2");
    			button2 = element("button");
    			button2.textContent = "Collapsible Group Item #3";
    			t11 = space();
    			div10 = element("div");
    			div9 = element("div");
    			div9.textContent = "And lastly, the placeholder content for the third\n                            and final accordion panel. This panel is hidden by\n                            default.";
    			t13 = space();
    			style = element("style");
    			attr_dev(button0, "class", "btn btn-link btn-block text-left");
    			attr_dev(button0, "type", "button");
    			attr_dev(button0, "data-toggle", "collapse");
    			attr_dev(button0, "data-target", "#collapseOne");
    			attr_dev(button0, "aria-expanded", "true");
    			attr_dev(button0, "aria-controls", "collapseOne");
    			add_location(button0, file, 11, 28, 409);
    			attr_dev(h20, "class", "mb-0");
    			add_location(h20, file, 10, 24, 363);
    			attr_dev(div0, "class", "card-header");
    			attr_dev(div0, "id", "headingOne");
    			add_location(div0, file, 9, 20, 297);
    			add_location(code, file, 31, 81, 1367);
    			attr_dev(div1, "class", "card-body");
    			add_location(div1, file, 29, 24, 1185);
    			attr_dev(div2, "id", "collapseOne");
    			attr_dev(div2, "class", "collapse show");
    			attr_dev(div2, "aria-labelledby", "headingOne");
    			attr_dev(div2, "data-parent", "#accordionExample");
    			add_location(div2, file, 23, 20, 938);
    			attr_dev(div3, "class", "card");
    			add_location(div3, file, 8, 16, 258);
    			attr_dev(button1, "class", "btn btn-link btn-block text-left collapsed");
    			attr_dev(button1, "type", "button");
    			attr_dev(button1, "data-toggle", "collapse");
    			attr_dev(button1, "data-target", "#collapseTwo");
    			attr_dev(button1, "aria-expanded", "false");
    			attr_dev(button1, "aria-controls", "collapseTwo");
    			add_location(button1, file, 40, 28, 1703);
    			attr_dev(h21, "class", "mb-0");
    			add_location(h21, file, 39, 24, 1657);
    			attr_dev(div4, "class", "card-header");
    			attr_dev(div4, "id", "headingTwo");
    			add_location(div4, file, 38, 20, 1591);
    			attr_dev(div5, "class", "card-body");
    			add_location(div5, file, 57, 24, 2484);
    			attr_dev(div6, "id", "collapseTwo");
    			attr_dev(div6, "class", "collapse");
    			attr_dev(div6, "aria-labelledby", "headingTwo");
    			attr_dev(div6, "data-parent", "#accordionExample");
    			add_location(div6, file, 51, 20, 2242);
    			attr_dev(div7, "class", "card");
    			add_location(div7, file, 37, 16, 1552);
    			attr_dev(button2, "class", "btn btn-link btn-block text-left collapsed");
    			attr_dev(button2, "type", "button");
    			attr_dev(button2, "data-toggle", "collapse");
    			attr_dev(button2, "data-target", "#collapseThree");
    			attr_dev(button2, "aria-expanded", "false");
    			attr_dev(button2, "aria-controls", "collapseThree");
    			add_location(button2, file, 66, 28, 2904);
    			attr_dev(h22, "class", "mb-0");
    			add_location(h22, file, 65, 24, 2858);
    			attr_dev(div8, "class", "card-header");
    			attr_dev(div8, "id", "headingThree");
    			add_location(div8, file, 64, 20, 2790);
    			attr_dev(div9, "class", "card-body");
    			add_location(div9, file, 83, 24, 3693);
    			attr_dev(div10, "id", "collapseThree");
    			attr_dev(div10, "class", "collapse");
    			attr_dev(div10, "aria-labelledby", "headingThree");
    			attr_dev(div10, "data-parent", "#accordionExample");
    			add_location(div10, file, 77, 20, 3447);
    			attr_dev(div11, "class", "card");
    			add_location(div11, file, 63, 16, 2751);
    			attr_dev(div12, "class", "accordion");
    			attr_dev(div12, "id", "accordionExample");
    			add_location(div12, file, 7, 12, 196);
    			add_location(style, file, 91, 12, 4023);
    			attr_dev(div13, "class", "");
    			add_location(div13, file, 6, 8, 169);
    			attr_dev(div14, "class", "card");
    			add_location(div14, file, 5, 4, 142);
    			attr_dev(div15, "class", "container-fluid");
    			set_style(div15, "padding-top", "0.5em");
    			add_location(div15, file, 4, 0, 81);
    		},
    		l: function claim(nodes) {
    			throw new Error("options.hydrate only works if the component was compiled with the `hydratable: true` option");
    		},
    		m: function mount(target, anchor) {
    			insert_dev(target, div15, anchor);
    			append_dev(div15, div14);
    			append_dev(div14, div13);
    			append_dev(div13, div12);
    			append_dev(div12, div3);
    			append_dev(div3, div0);
    			append_dev(div0, h20);
    			append_dev(h20, button0);
    			append_dev(div3, t1);
    			append_dev(div3, div2);
    			append_dev(div2, div1);
    			append_dev(div1, t2);
    			append_dev(div1, code);
    			append_dev(div1, t4);
    			append_dev(div12, t5);
    			append_dev(div12, div7);
    			append_dev(div7, div4);
    			append_dev(div4, h21);
    			append_dev(h21, button1);
    			append_dev(div7, t7);
    			append_dev(div7, div6);
    			append_dev(div6, div5);
    			append_dev(div12, t9);
    			append_dev(div12, div11);
    			append_dev(div11, div8);
    			append_dev(div8, h22);
    			append_dev(h22, button2);
    			append_dev(div11, t11);
    			append_dev(div11, div10);
    			append_dev(div10, div9);
    			append_dev(div13, t13);
    			append_dev(div13, style);
    		},
    		p: noop,
    		i: noop,
    		o: noop,
    		d: function destroy(detaching) {
    			if (detaching) detach_dev(div15);
    		}
    	};

    	dispatch_dev("SvelteRegisterBlock", {
    		block,
    		id: create_fragment.name,
    		type: "component",
    		source: "",
    		ctx
    	});

    	return block;
    }

    function instance($$self, $$props, $$invalidate) {
    	let { $$slots: slots = {}, $$scope } = $$props;
    	validate_slots("Result", slots, []);
    	let { resultList } = $$props;
    	console.log("hello result");
    	const writable_props = ["resultList"];

    	Object.keys($$props).forEach(key => {
    		if (!~writable_props.indexOf(key) && key.slice(0, 2) !== "$$") console_1.warn(`<Result> was created with unknown prop '${key}'`);
    	});

    	$$self.$$set = $$props => {
    		if ("resultList" in $$props) $$invalidate(0, resultList = $$props.resultList);
    	};

    	$$self.$capture_state = () => ({ resultList });

    	$$self.$inject_state = $$props => {
    		if ("resultList" in $$props) $$invalidate(0, resultList = $$props.resultList);
    	};

    	if ($$props && "$$inject" in $$props) {
    		$$self.$inject_state($$props.$$inject);
    	}

    	return [resultList];
    }

    class Result extends SvelteComponentDev {
    	constructor(options) {
    		super(options);
    		init(this, options, instance, create_fragment, safe_not_equal, { resultList: 0 });

    		dispatch_dev("SvelteRegisterComponent", {
    			component: this,
    			tagName: "Result",
    			options,
    			id: create_fragment.name
    		});

    		const { ctx } = this.$$;
    		const props = options.props || {};

    		if (/*resultList*/ ctx[0] === undefined && !("resultList" in props)) {
    			console_1.warn("<Result> was created without expected prop 'resultList'");
    		}
    	}

    	get resultList() {
    		throw new Error("<Result>: Props cannot be read directly from the component instance unless compiling with 'accessors: true' or '<svelte:options accessors/>'");
    	}

    	set resultList(value) {
    		throw new Error("<Result>: Props cannot be set directly on the component instance unless compiling with 'accessors: true' or '<svelte:options accessors/>'");
    	}
    }

    /* src/App.svelte generated by Svelte v3.31.2 */

    const { console: console_1$1 } = globals;
    const file$1 = "src/App.svelte";

    // (85:4) {#if mode === "testssl"}
    function create_if_block_2(ctx) {
    	let div10;
    	let div9;
    	let form;
    	let div3;
    	let div2;
    	let div0;
    	let button0;
    	let t1;
    	let input0;
    	let t2;
    	let div1;
    	let button1;
    	let t4;
    	let div8;
    	let div5;
    	let div4;
    	let input1;
    	let t5;
    	let label0;
    	let t7;
    	let div7;
    	let div6;
    	let input2;
    	let t8;
    	let label1;
    	let mounted;
    	let dispose;

    	const block = {
    		c: function create() {
    			div10 = element("div");
    			div9 = element("div");
    			form = element("form");
    			div3 = element("div");
    			div2 = element("div");
    			div0 = element("div");
    			button0 = element("button");
    			button0.textContent = "?";
    			t1 = space();
    			input0 = element("input");
    			t2 = space();
    			div1 = element("div");
    			button1 = element("button");
    			button1.textContent = "Test SSL";
    			t4 = space();
    			div8 = element("div");
    			div5 = element("div");
    			div4 = element("div");
    			input1 = element("input");
    			t5 = space();
    			label0 = element("label");
    			label0.textContent = "Hints";
    			t7 = space();
    			div7 = element("div");
    			div6 = element("div");
    			input2 = element("input");
    			t8 = space();
    			label1 = element("label");
    			label1.textContent = "Default checkbox";
    			attr_dev(button0, "class", "btn btn-outline-secondary");
    			attr_dev(button0, "type", "button");
    			attr_dev(button0, "id", "button-addon1");
    			add_location(button0, file$1, 91, 32, 3221);
    			attr_dev(div0, "class", "input-group-prepend");
    			add_location(div0, file$1, 90, 28, 3155);
    			attr_dev(input0, "type", "text");
    			attr_dev(input0, "class", "form-control");
    			attr_dev(input0, "placeholder", "URL / Hostname");
    			add_location(input0, file$1, 97, 28, 3511);
    			attr_dev(button1, "class", "btn btn-outline-primary");
    			attr_dev(button1, "type", "button");
    			attr_dev(button1, "id", "button-addon2");
    			add_location(button1, file$1, 104, 32, 3863);
    			attr_dev(div1, "class", "input-group-append");
    			add_location(div1, file$1, 103, 28, 3798);
    			attr_dev(div2, "class", "input-group mb-3");
    			add_location(div2, file$1, 89, 24, 3096);
    			attr_dev(div3, "class", "row");
    			add_location(div3, file$1, 88, 20, 3054);
    			attr_dev(input1, "class", "form-check-input");
    			attr_dev(input1, "type", "checkbox");
    			input1.__value = "";
    			input1.value = input1.__value;
    			attr_dev(input1, "id", "defaultCheck1");
    			add_location(input1, file$1, 116, 32, 4411);
    			attr_dev(label0, "class", "form-check-label");
    			attr_dev(label0, "for", "defaultCheck1");
    			add_location(label0, file$1, 123, 32, 4769);
    			attr_dev(div4, "class", "form-check");
    			add_location(div4, file$1, 115, 28, 4354);
    			attr_dev(div5, "class", "col");
    			add_location(div5, file$1, 114, 24, 4308);
    			attr_dev(input2, "class", "form-check-input");
    			attr_dev(input2, "type", "checkbox");
    			input2.value = "";
    			attr_dev(input2, "id", "defaultCheck1");
    			add_location(input2, file$1, 130, 32, 5102);
    			attr_dev(label1, "class", "form-check-label");
    			attr_dev(label1, "for", "defaultCheck1");
    			add_location(label1, file$1, 136, 32, 5389);
    			attr_dev(div6, "class", "form-check");
    			add_location(div6, file$1, 129, 28, 5045);
    			attr_dev(div7, "class", "col");
    			add_location(div7, file$1, 128, 24, 4999);
    			attr_dev(div8, "class", "row");
    			add_location(div8, file$1, 113, 20, 4266);
    			add_location(form, file$1, 87, 16, 3027);
    			attr_dev(div9, "class", "card-body");
    			add_location(div9, file$1, 86, 12, 2987);
    			attr_dev(div10, "class", "tab-pane svelte-1kdyxy4");
    			add_location(div10, file$1, 85, 8, 2952);
    		},
    		m: function mount(target, anchor) {
    			insert_dev(target, div10, anchor);
    			append_dev(div10, div9);
    			append_dev(div9, form);
    			append_dev(form, div3);
    			append_dev(div3, div2);
    			append_dev(div2, div0);
    			append_dev(div0, button0);
    			append_dev(div2, t1);
    			append_dev(div2, input0);
    			set_input_value(input0, /*testSSLOption*/ ctx[1].url);
    			append_dev(div2, t2);
    			append_dev(div2, div1);
    			append_dev(div1, button1);
    			append_dev(form, t4);
    			append_dev(form, div8);
    			append_dev(div8, div5);
    			append_dev(div5, div4);
    			append_dev(div4, input1);
    			input1.checked = /*testSSLOption*/ ctx[1].hints;
    			append_dev(div4, t5);
    			append_dev(div4, label0);
    			append_dev(div8, t7);
    			append_dev(div8, div7);
    			append_dev(div7, div6);
    			append_dev(div6, input2);
    			append_dev(div6, t8);
    			append_dev(div6, label1);

    			if (!mounted) {
    				dispose = [
    					listen_dev(input0, "input", /*input0_input_handler*/ ctx[6]),
    					listen_dev(button1, "click", /*runTestSSL*/ ctx[2], false, false, false),
    					listen_dev(input1, "change", /*input1_change_handler*/ ctx[7])
    				];

    				mounted = true;
    			}
    		},
    		p: function update(ctx, dirty) {
    			if (dirty & /*testSSLOption*/ 2 && input0.value !== /*testSSLOption*/ ctx[1].url) {
    				set_input_value(input0, /*testSSLOption*/ ctx[1].url);
    			}

    			if (dirty & /*testSSLOption*/ 2) {
    				input1.checked = /*testSSLOption*/ ctx[1].hints;
    			}
    		},
    		d: function destroy(detaching) {
    			if (detaching) detach_dev(div10);
    			mounted = false;
    			run_all(dispose);
    		}
    	};

    	dispatch_dev("SvelteRegisterBlock", {
    		block,
    		id: create_if_block_2.name,
    		type: "if",
    		source: "(85:4) {#if mode === \\\"testssl\\\"}",
    		ctx
    	});

    	return block;
    }

    // (149:4) {#if mode === "ping"}
    function create_if_block_1(ctx) {
    	let div1;
    	let div0;

    	const block = {
    		c: function create() {
    			div1 = element("div");
    			div0 = element("div");
    			div0.textContent = "Ping";
    			attr_dev(div0, "class", "card-body");
    			add_location(div0, file$1, 150, 12, 5838);
    			attr_dev(div1, "class", "tab-pane svelte-1kdyxy4");
    			add_location(div1, file$1, 149, 8, 5803);
    		},
    		m: function mount(target, anchor) {
    			insert_dev(target, div1, anchor);
    			append_dev(div1, div0);
    		},
    		d: function destroy(detaching) {
    			if (detaching) detach_dev(div1);
    		}
    	};

    	dispatch_dev("SvelteRegisterBlock", {
    		block,
    		id: create_if_block_1.name,
    		type: "if",
    		source: "(149:4) {#if mode === \\\"ping\\\"}",
    		ctx
    	});

    	return block;
    }

    // (154:4) {#if mode === "traceroute"}
    function create_if_block(ctx) {
    	let div1;
    	let div0;

    	const block = {
    		c: function create() {
    			div1 = element("div");
    			div0 = element("div");
    			div0.textContent = "Trace Route";
    			attr_dev(div0, "class", "card-body");
    			add_location(div0, file$1, 155, 12, 5972);
    			attr_dev(div1, "class", "tab-pane svelte-1kdyxy4");
    			add_location(div1, file$1, 154, 8, 5937);
    		},
    		m: function mount(target, anchor) {
    			insert_dev(target, div1, anchor);
    			append_dev(div1, div0);
    		},
    		d: function destroy(detaching) {
    			if (detaching) detach_dev(div1);
    		}
    	};

    	dispatch_dev("SvelteRegisterBlock", {
    		block,
    		id: create_if_block.name,
    		type: "if",
    		source: "(154:4) {#if mode === \\\"traceroute\\\"}",
    		ctx
    	});

    	return block;
    }

    function create_fragment$1(ctx) {
    	let h1;
    	let t1;
    	let div;
    	let ul;
    	let li0;
    	let a0;
    	let t2;
    	let a0_class_value;
    	let t3;
    	let li1;
    	let a1;
    	let t4;
    	let a1_class_value;
    	let t5;
    	let li2;
    	let a2;
    	let t6;
    	let a2_class_value;
    	let t7;
    	let t8;
    	let t9;
    	let t10;
    	let result;
    	let current;
    	let mounted;
    	let dispose;
    	let if_block0 = /*mode*/ ctx[0] === "testssl" && create_if_block_2(ctx);
    	let if_block1 = /*mode*/ ctx[0] === "ping" && create_if_block_1(ctx);
    	let if_block2 = /*mode*/ ctx[0] === "traceroute" && create_if_block(ctx);
    	result = new Result({ $$inline: true });

    	const block = {
    		c: function create() {
    			h1 = element("h1");
    			h1.textContent = "Web Net Tools";
    			t1 = space();
    			div = element("div");
    			ul = element("ul");
    			li0 = element("li");
    			a0 = element("a");
    			t2 = text("Test SSL");
    			t3 = space();
    			li1 = element("li");
    			a1 = element("a");
    			t4 = text("Ping");
    			t5 = space();
    			li2 = element("li");
    			a2 = element("a");
    			t6 = text("Trace Route");
    			t7 = space();
    			if (if_block0) if_block0.c();
    			t8 = space();
    			if (if_block1) if_block1.c();
    			t9 = space();
    			if (if_block2) if_block2.c();
    			t10 = space();
    			create_component(result.$$.fragment);
    			add_location(h1, file$1, 59, 0, 2041);
    			attr_dev(a0, "class", a0_class_value = "nav-link " + (/*mode*/ ctx[0] === "testssl" ? "active" : ""));
    			attr_dev(a0, "href", "#");
    			add_location(a0, file$1, 63, 12, 2212);
    			attr_dev(li0, "class", "nav-item");
    			attr_dev(li0, "role", "presentation");
    			add_location(li0, file$1, 62, 8, 2158);
    			attr_dev(a1, "class", a1_class_value = "nav-link " + (/*mode*/ ctx[0] === "ping" ? "active" : ""));
    			attr_dev(a1, "href", "#");
    			add_location(a1, file$1, 70, 12, 2464);
    			attr_dev(li1, "class", "nav-item");
    			attr_dev(li1, "role", "presentation");
    			add_location(li1, file$1, 69, 8, 2410);
    			attr_dev(a2, "class", a2_class_value = "nav-link " + (/*mode*/ ctx[0] === "traceroute" ? "active" : ""));
    			attr_dev(a2, "href", "#");
    			add_location(a2, file$1, 77, 12, 2706);
    			attr_dev(li2, "class", "nav-item");
    			attr_dev(li2, "role", "presentation");
    			add_location(li2, file$1, 76, 8, 2652);
    			attr_dev(ul, "class", "nav nav-tabs svelte-1kdyxy4");
    			attr_dev(ul, "id", "myTab");
    			attr_dev(ul, "role", "tablist");
    			add_location(ul, file$1, 61, 4, 2098);
    			attr_dev(div, "class", "container-fluid");
    			add_location(div, file$1, 60, 0, 2064);
    		},
    		l: function claim(nodes) {
    			throw new Error("options.hydrate only works if the component was compiled with the `hydratable: true` option");
    		},
    		m: function mount(target, anchor) {
    			insert_dev(target, h1, anchor);
    			insert_dev(target, t1, anchor);
    			insert_dev(target, div, anchor);
    			append_dev(div, ul);
    			append_dev(ul, li0);
    			append_dev(li0, a0);
    			append_dev(a0, t2);
    			append_dev(ul, t3);
    			append_dev(ul, li1);
    			append_dev(li1, a1);
    			append_dev(a1, t4);
    			append_dev(ul, t5);
    			append_dev(ul, li2);
    			append_dev(li2, a2);
    			append_dev(a2, t6);
    			append_dev(div, t7);
    			if (if_block0) if_block0.m(div, null);
    			append_dev(div, t8);
    			if (if_block1) if_block1.m(div, null);
    			append_dev(div, t9);
    			if (if_block2) if_block2.m(div, null);
    			insert_dev(target, t10, anchor);
    			mount_component(result, target, anchor);
    			current = true;

    			if (!mounted) {
    				dispose = [
    					listen_dev(a0, "click", /*click_handler*/ ctx[3], false, false, false),
    					listen_dev(a1, "click", /*click_handler_1*/ ctx[4], false, false, false),
    					listen_dev(a2, "click", /*click_handler_2*/ ctx[5], false, false, false)
    				];

    				mounted = true;
    			}
    		},
    		p: function update(ctx, [dirty]) {
    			if (!current || dirty & /*mode*/ 1 && a0_class_value !== (a0_class_value = "nav-link " + (/*mode*/ ctx[0] === "testssl" ? "active" : ""))) {
    				attr_dev(a0, "class", a0_class_value);
    			}

    			if (!current || dirty & /*mode*/ 1 && a1_class_value !== (a1_class_value = "nav-link " + (/*mode*/ ctx[0] === "ping" ? "active" : ""))) {
    				attr_dev(a1, "class", a1_class_value);
    			}

    			if (!current || dirty & /*mode*/ 1 && a2_class_value !== (a2_class_value = "nav-link " + (/*mode*/ ctx[0] === "traceroute" ? "active" : ""))) {
    				attr_dev(a2, "class", a2_class_value);
    			}

    			if (/*mode*/ ctx[0] === "testssl") {
    				if (if_block0) {
    					if_block0.p(ctx, dirty);
    				} else {
    					if_block0 = create_if_block_2(ctx);
    					if_block0.c();
    					if_block0.m(div, t8);
    				}
    			} else if (if_block0) {
    				if_block0.d(1);
    				if_block0 = null;
    			}

    			if (/*mode*/ ctx[0] === "ping") {
    				if (if_block1) ; else {
    					if_block1 = create_if_block_1(ctx);
    					if_block1.c();
    					if_block1.m(div, t9);
    				}
    			} else if (if_block1) {
    				if_block1.d(1);
    				if_block1 = null;
    			}

    			if (/*mode*/ ctx[0] === "traceroute") {
    				if (if_block2) ; else {
    					if_block2 = create_if_block(ctx);
    					if_block2.c();
    					if_block2.m(div, null);
    				}
    			} else if (if_block2) {
    				if_block2.d(1);
    				if_block2 = null;
    			}
    		},
    		i: function intro(local) {
    			if (current) return;
    			transition_in(result.$$.fragment, local);
    			current = true;
    		},
    		o: function outro(local) {
    			transition_out(result.$$.fragment, local);
    			current = false;
    		},
    		d: function destroy(detaching) {
    			if (detaching) detach_dev(h1);
    			if (detaching) detach_dev(t1);
    			if (detaching) detach_dev(div);
    			if (if_block0) if_block0.d();
    			if (if_block1) if_block1.d();
    			if (if_block2) if_block2.d();
    			if (detaching) detach_dev(t10);
    			destroy_component(result, detaching);
    			mounted = false;
    			run_all(dispose);
    		}
    	};

    	dispatch_dev("SvelteRegisterBlock", {
    		block,
    		id: create_fragment$1.name,
    		type: "component",
    		source: "",
    		ctx
    	});

    	return block;
    }

    function instance$1($$self, $$props, $$invalidate) {
    	let { $$slots: slots = {}, $$scope } = $$props;
    	validate_slots("App", slots, []);

    	var __awaiter = this && this.__awaiter || function (thisArg, _arguments, P, generator) {
    		function adopt(value) {
    			return value instanceof P
    			? value
    			: new P(function (resolve) {
    						resolve(value);
    					});
    		}

    		return new (P || (P = Promise))(function (resolve, reject) {
    				function fulfilled(value) {
    					try {
    						step(generator.next(value));
    					} catch(e) {
    						reject(e);
    					}
    				}

    				function rejected(value) {
    					try {
    						step(generator["throw"](value));
    					} catch(e) {
    						reject(e);
    					}
    				}

    				function step(result) {
    					result.done
    					? resolve(result.value)
    					: adopt(result.value).then(fulfilled, rejected);
    				}

    				step((generator = generator.apply(thisArg, _arguments || [])).next());
    			});
    	};

    	/*
    let url = "mobile.psi-mt.de";
    let result = "";
    let result2 = "";
    const ansi_up = new AnsiUp();

    async function checkURL() {
        result = "";
        result2 = "";
        const response = await fetch("/testssl", {
            method: "POST",
            body: JSON.stringify({ url: url }),
            headers: {
                "Content-Type": "application/json",
                Accept: "text/plain",
            },
        });

        if (response.ok && response.body !== null) {
            const reader = response.body
                .pipeThrough(new TextDecoderStream())
                .getReader();
            while (true) {
                const { value, done } = await reader.read();
                if (done) break;
                result += value;
                result2 = ansi_up.ansi_to_html(result);
            }
        }
    }
    */
    	let mode = "testssl";

    	let testSSLOption = { url: "", hints: true };

    	function runTestSSL() {
    		return __awaiter(this, void 0, void 0, function* () {
    			console.log("runTestSSL", testSSLOption);

    			const response = yield fetch("/testssl", {
    				method: "POST",
    				body: JSON.stringify({ url }),
    				headers: {
    					"Content-Type": "application/json",
    					Accept: "text/plain"
    				}
    			});
    		});
    	}

    	const writable_props = [];

    	Object.keys($$props).forEach(key => {
    		if (!~writable_props.indexOf(key) && key.slice(0, 2) !== "$$") console_1$1.warn(`<App> was created with unknown prop '${key}'`);
    	});

    	const click_handler = () => $$invalidate(0, mode = "testssl");
    	const click_handler_1 = () => $$invalidate(0, mode = "ping");
    	const click_handler_2 = () => $$invalidate(0, mode = "traceroute");

    	function input0_input_handler() {
    		testSSLOption.url = this.value;
    		$$invalidate(1, testSSLOption);
    	}

    	function input1_change_handler() {
    		testSSLOption.hints = this.checked;
    		$$invalidate(1, testSSLOption);
    	}

    	$$self.$capture_state = () => ({
    		__awaiter,
    		AnsiUp,
    		Result,
    		mode,
    		testSSLOption,
    		runTestSSL
    	});

    	$$self.$inject_state = $$props => {
    		if ("__awaiter" in $$props) __awaiter = $$props.__awaiter;
    		if ("mode" in $$props) $$invalidate(0, mode = $$props.mode);
    		if ("testSSLOption" in $$props) $$invalidate(1, testSSLOption = $$props.testSSLOption);
    	};

    	if ($$props && "$$inject" in $$props) {
    		$$self.$inject_state($$props.$$inject);
    	}

    	return [
    		mode,
    		testSSLOption,
    		runTestSSL,
    		click_handler,
    		click_handler_1,
    		click_handler_2,
    		input0_input_handler,
    		input1_change_handler
    	];
    }

    class App extends SvelteComponentDev {
    	constructor(options) {
    		super(options);
    		init(this, options, instance$1, create_fragment$1, safe_not_equal, {});

    		dispatch_dev("SvelteRegisterComponent", {
    			component: this,
    			tagName: "App",
    			options,
    			id: create_fragment$1.name
    		});
    	}
    }

    var app = new App({
        target: document.body
    });

    return app;

}());
//# sourceMappingURL=bundle.js.map
