'use strict';

angular.module('digital.format.account', [])

    .filter('formatMillier', ['$filter', function ($filter) {

        return function (input) {

            if (!input) return "";

            var nombre=input;

            nombre += '';

            var sep = ' ';

            var reg = /(\d+)(\d{3})/;

            while (reg.test(nombre)) {

                nombre = nombre.replace(reg, '$1' + sep + '$2');
            }

            return nombre;
        };
    }]);