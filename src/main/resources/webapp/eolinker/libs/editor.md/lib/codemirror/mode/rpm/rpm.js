!function(e){"object"==typeof exports&&"object"==typeof module?e(require("../../lib/codemirror")):"function"==typeof define&&define.amd?define(["../../lib/codemirror"],e):e(CodeMirror)}(function(e){"use strict";e.defineMode("rpm-changes",function(){var e=/^-+$/,r=/^(Mon|Tue|Wed|Thu|Fri|Sat|Sun) (Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)  ?\d{1,2} \d{2}:\d{2}(:\d{2})? [A-Z]{3,4} \d{4} - /,t=/^[\w+.-]+@[\w.-]+/;return{token:function(n){if(n.sol()){if(n.match(e))return"tag";if(n.match(r))return"tag"}return n.match(t)?"string":(n.next(),null)}}}),e.defineMIME("text/x-rpm-changes","rpm-changes"),e.defineMode("rpm-spec",function(){var e=/^(i386|i586|i686|x86_64|ppc64|ppc|ia64|s390x|s390|sparc64|sparcv9|sparc|noarch|alphaev6|alpha|hppa|mipsel)/,r=/^(Name|Version|Release|License|Summary|Url|Group|Source|BuildArch|BuildRequires|BuildRoot|AutoReqProv|Provides|Requires(\(\w+\))?|Obsoletes|Conflicts|Recommends|Source\d*|Patch\d*|ExclusiveArch|NoSource|Supplements):/,t=/^%(debug_package|package|description|prep|build|install|files|clean|changelog|preinstall|preun|postinstall|postun|pre|post|triggerin|triggerun|pretrans|posttrans|verifyscript|check|triggerpostun|triggerprein|trigger)/,n=/^%(ifnarch|ifarch|if)/,i=/^%(else|endif)/,o=/^(\!|\?|\<\=|\<|\>\=|\>|\=\=|\&\&|\|\|)/;return{startState:function(){return{controlFlow:!1,macroParameters:!1,section:!1}},token:function(c,a){var u=c.peek();if("#"==u)return c.skipToEnd(),"comment";if(c.sol()){if(c.match(r))return"preamble";if(c.match(t))return"section"}if(c.match(/^\$\w+/))return"def";if(c.match(/^\$\{\w+\}/))return"def";if(c.match(i))return"keyword";if(c.match(n))return a.controlFlow=!0,"keyword";if(a.controlFlow){if(c.match(o))return"operator";if(c.match(/^(\d+)/))return"number";c.eol()&&(a.controlFlow=!1)}if(c.match(e))return"number";if(c.match(/^%[\w]+/))return c.match(/^\(/)&&(a.macroParameters=!0),"macro";if(a.macroParameters){if(c.match(/^\d+/))return"number";if(c.match(/^\)/))return a.macroParameters=!1,"macro"}return c.match(/^%\{\??[\w \-]+\}/)?"macro":(c.next(),null)}}}),e.defineMIME("text/x-rpm-spec","rpm-spec")});