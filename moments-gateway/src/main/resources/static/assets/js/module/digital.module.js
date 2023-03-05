var DiGital = angular.module('DiGital', [
    'ngAlertify',
    'ngAnimate',
    //'acute.select',
    'ui.bootstrap',
    'ui.router',
    'pascalprecht.translate',
    'ngFileUpload',
    'datatables',
    'datatables.bootstrap',
    'datatables.colreorder',
    'datatables.colvis',
    'datatables.tabletools',
    'datatables.scroller',
    'datatables.columnfilter',
    'moment-picker',
    'ngCookies',
    'digital.number-format',
    'digital.format.account',
    'digital.services',
    'ui.mask',
    'mdo-angular-cryptography',

    //'AxelSoft'

    //'imhere-angular-wizard',
])

.constant('ROOT_URL', {

    pathImage: 'C:/data-digital/logo',

    logoPhoenix: "C:/data-digital/logo/logo_phoenix_acces.png",

    arrierePlan: "C:/data-digital/logo/devis.png",

    //Dev
    api: '/api/digital-gateway',
    absolute: 'views',

    //Prod
    /*api: '/digital-web/api/digital-gateway',
    absolute: '../digital-web/views'*/
});

DiGital

    .config(['$stateProvider', '$urlRouterProvider', '$locationProvider', '$qProvider', 'ROOT_URL',

        function($stateProvider, $urlRouterProvider, $locationProvider, $qProvider, ROOT_URL) {

            $stateProvider
                .state('/login', {
                    url: '/login',
                    templateUrl: ROOT_URL.absolute+'/login/login.html',
                    controller: 'LoginController'
                });

            $urlRouterProvider.otherwise('/login');

            $locationProvider.hashPrefix('');

            $qProvider.errorOnUnhandledRejections(false);
        }
    ])


    .directive('formats', ['$filter', function ($filter) {

        return {

            require: '?ngModel',

            link: function (scope, elem, attrs, ctrl) {
                if (!ctrl) return;


                ctrl.$formatters.unshift(function (a) {
                    return $filter(attrs.format)(ctrl.$modelValue)
                });


                ctrl.$parsers.unshift(function (viewValue) {
                    elem.priceFormat({
                        prefix: '',
                        thousandsSeparator: ' '
                    });

                    return elem[0].value;
                });
            }
        };
    }])


    .config(function (datepickerConfig, datepickerPopupConfig) {

        datepickerPopupConfig.showButtonBar = false;

        datepickerConfig.startingDay = 1;

        datepickerConfig.showWeeks = true;

    })


    .config(function($cryptoProvider){
        $cryptoProvider.setCryptographyKey('$*****@&__$');
    })


    .directive('autocomplete', function($timeout, ROOT_URL) {

        return {
            controller: 'autocompleteController',
            restrict: 'E',
            replace: true,
            scope: {
                choices: '=',
                enteredtext: '=',
                minlength: '=',
                selected: '='
            },
            templateUrl: ROOT_URL.absolute+'/services/service-courier/envoi-colis/autocomplete.html'
        }
    })


    .directive('autocompletes', function($timeout, ROOT_URL) {

        return {
            controller: 'autocomplete2Controller',
            restrict: 'E',
            replace: true,
            scope: {
                choices: '=',
                enteredtext: '=',
                minlength: '=',
                selected2: '='
            },
            templateUrl: ROOT_URL.absolute+'/services/service-courier/envoi-colis/autocomplete2.html'
        }
    })


    .directive('autocomplet', function($timeout, ROOT_URL) {

        return {
            controller: 'AutoCompletController',
            restrict: 'E',
            replace: true,
            scope: {
                choices: '=',
                enteredtext: '=',
                minlength: '=',
                selected: '='
            },
            templateUrl: ROOT_URL.absolute+'/auto-complet/frm-auto-complet.html'
        }
    })


    .directive('numberInput', function($filter) {

        return {
            require: 'ngModel',
            link: function(scope, elem, attrs, ngModelCtrl) {

                ngModelCtrl.$formatters.push(function(modelValue) {
                    return setDisplayNumber(modelValue, true);
                });

                ngModelCtrl.$parsers.push(function(viewValue) {
                    setDisplayNumber(viewValue);
                    return setModelNumber(viewValue);
                });

                elem.bind('keyup focus', function() {
                    setDisplayNumber(elem.val());
                });

                function setDisplayNumber(val, formatter) {
                    var valStr, displayValue;

                    if (typeof val === 'undefined') {
                        return 0;
                    }

                    valStr = val.toString();
                    displayValue = valStr.replace(/,/g, '').replace(/[A-Za-z]/g, '');
                    displayValue = parseFloat(displayValue);
                    displayValue = (!isNaN(displayValue)) ? displayValue.toString() : '';

                    // handle leading character -/0
                    if (valStr.length === 1 && valStr[0] === '-') {
                        displayValue = valStr[0];
                    } else if (valStr.length === 1 && valStr[0] === '0') {
                        displayValue = '';
                    } else {
                        displayValue = $filter('number')(displayValue);
                    }

                    // handle decimal
                    if (!attrs.integer) {
                        if (displayValue.indexOf('.') === -1) {
                            if (valStr.slice(-1) === '.') {
                                displayValue += '.';
                            } else if (valStr.slice(-2) === '.0') {
                                displayValue += '.0';
                            } else if (valStr.slice(-3) === '.00') {
                                displayValue += '.00';
                            }
                        } // handle last character 0 after decimal and another number
                        else {
                            if (valStr.slice(-1) === '0') {
                                displayValue += '0';
                            }
                        }
                    }

                    if (attrs.positive && displayValue[0] === '-') {
                        displayValue = displayValue.substring(1);
                    }

                    if (typeof formatter !== 'undefined') {
                        return (displayValue === '') ? 0 : displayValue;
                    } else {
                        elem.val((displayValue === '0') ? '' : displayValue);
                    }
                }

                function setModelNumber(val) {
                    var modelNum = val.toString().replace(/,/g, '').replace(/[A-Za-z]/g, '');
                    modelNum = parseFloat(modelNum);
                    modelNum = (!isNaN(modelNum)) ? modelNum : 0;
                    if (modelNum.toString().indexOf('.') !== -1) {
                        modelNum = Math.round((modelNum + 0.00001) * 100) / 100;
                    }
                    if (attrs.positive) {
                        modelNum = Math.abs(modelNum);
                    }
                    return modelNum;
                }
            }
        };
    })


    .filter('split', function() {
        return function(input, splitChar, splitIndex) {
            // do some bounds checking here to ensure it has that index
            return input.split(splitChar)[splitIndex];
        }
    })

    .filter('tel', function () {

        return function (tel) {
            if (!tel) { return ''; }

            var value = tel.toString().trim().replace(/^\+/, '');

            if (value.match(/[^0-9]/)) {
                return tel;
            }

            var country, city, number;

            switch (value.length) {
                case 10: // +1PPP####### -> C (PPP) ###-####
                    country = 1;
                    city = value.slice(0, 3);
                    number = value.slice(3);
                    break;

                case 11: // +CPPP####### -> CCC (PP) ###-####
                    country = value[0];
                    city = value.slice(1, 4);
                    number = value.slice(4);
                    break;

                case 12: // +CCCPP####### -> CCC (PP) ###-####
                    country = value.slice(0, 3);
                    city = value.slice(3, 5);
                    number = value.slice(5);
                    break;

                default:
                    return tel;
            }

            if (country == 1) {
                country = "";
            }

            number = number.slice(0, 3) + '-' + number.slice(3);

            return (country + " (" + city + ") " + number).trim();
        };
    })


    .filter('capitalize', function() {
        return function(input, scope) {
            if (input!=null)
                input = input.toLowerCase();
            return input.substring(0,1).toUpperCase()+input.substring(1).toLowerCase();
        }
    })


    .filter("timeago", function () {

        return function (time, local, raw) {
            if (!time) return "never";

            if (!local) {
                (local = Date.now())
            }

            if (angular.isDate(time)) {
                time = time.getTime();
            } else if (typeof time === "string") {
                time = new Date(time).getTime();
            }

            if (angular.isDate(local)) {
                local = local.getTime();
            }else if (typeof local === "string") {
                local = new Date(local).getTime();
            }

            if (typeof time !== 'number' || typeof local !== 'number') {
                return;
            }

            var
                offset = Math.abs((local - time) / 1000),
                span = [],
                MINUTE = 60,
                HOUR = 3600,
                DAY = 86400,
                WEEK = 604800,
                MONTH = 2629744,
                YEAR = 31556926,
                DECADE = 315569260;

            if (offset <= MINUTE)              span = [ '', raw ? 'maintenant' : 'moins d\'une minute' ];
            else if (offset < (MINUTE * 60))   span = [ Math.round(Math.abs(offset / MINUTE)), 'min' ];
            else if (offset < (HOUR * 24))     span = [ Math.round(Math.abs(offset / HOUR)), 'heure' ];
            else if (offset < (DAY * 7))       span = [ Math.round(Math.abs(offset / DAY)), 'jour' ];
            else if (offset < (WEEK * 52))     span = [ Math.round(Math.abs(offset / WEEK)), 'semaine' ];
            else if (offset < (YEAR * 10))     span = [ Math.round(Math.abs(offset / YEAR)), 'année' ];
            else if (offset < (DECADE * 100))  span = [ Math.round(Math.abs(offset / DECADE)), 'décennie' ];
            else                               span = [ '', 'un long moment' ];

            span[1] += (span[0] === 0 || span[0] > 1) ? 's' : '';
            span = span.join(' ');

            if (raw === true) {
                return span;
            }
            return (time <= local) ? 'il y a ' + span : span + 'à ' ;
        }
    })


    .directive('autocompleteclient', function($timeout, ROOT_URL) {
        return {
            controller: 'autoCpletClientController',
            restrict: 'E',
            replace: true,
            scope: {
                choicesA: '=',
                enteredtextA: '=',
                minlength: '=',
                selectedA: '='
            },
            templateUrl: ROOT_URL.absolute+'/auto-complet/frm-auto-complet-client.html'
        }
    })

    .filter('truncate', function () {
        return function (total_val, changword, max, tail) {
            if (!total_val) return '';
            max = parseInt(max, 10);
            if (!max) return total_val;
            if (total_val.length <= max) return total_val;

            total_val = total_val.substr(0, max);
            if (changword) {
                var space_last = total_val.lastIndexOf(' ');
                if (space_last != -1) {
                    total_val = total_val.substr(0, space_last);
                }
            }
            return total_val + (tail || ' …');
        };
    })

    .directive('autocompletezonelivraison', function($timeout, ROOT_URL) {
        return {
            controller: 'autoCpletZoneLivraisonController',
            restrict: 'E',
            replace: true,
            scope: {
                choicesZ: '=',
                enteredtextZ: '=',
                minlength: '=',
                selectedZ: '='
            },
            templateUrl: ROOT_URL.absolute+'/auto-complet/frm-auto-complet-zone-livraison.html'
        }
    })


    .directive('autocompletelivreur', function($timeout, ROOT_URL) {
        return {
            controller: 'autoCompletLivreurController',
            restrict: 'E',
            replace: true,
            scope: {
                choicesL: '=',
                enteredtextL: '=',
                minlength: '=',
                selectedL: '='
            },
            templateUrl: ROOT_URL.absolute+'/auto-complet/frm-auto-complet-livreur.html'
        }
    })

    ;

