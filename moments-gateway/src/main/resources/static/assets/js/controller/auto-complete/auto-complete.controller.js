(function() {

    'use strict';

    DiGital

        .controller('autoCpletClientController', function($scope, $cookies) {

            $scope.filteredListA = [];
            $scope.isVisibleA = {
                suggestions: false
            };

            $scope.filterItemsA = function () {
                if($scope.minlength <= $scope.enteredtextA.length) {
                    $scope.filteredListA            = querySearch($scope.enteredtextA);
                    $scope.isVisibleA.suggestions   = $scope.filteredListA.length > 0 ? true : false;
                }
                else {
                    $scope.isVisibleA.suggestions   = false;
                }
            };


            $scope.selectItemA = function (index) {
                $cookies.remove('uClient');
                //$scope.selectedA                = $scope.choicesA[index - 1];
                //console.log(index)
                //console.log($scope.choicesA)
                //console.log($scope.choicesA[index - 2])
                $scope.enteredtextA             = $scope.choicesA[index - 2].label;
                $scope.isVisibleA.suggestions   = false;
                $cookies.putObject('uClient', $scope.choicesA[index - 2].index);
            };


            function querySearch (query) {
                // returns list of filtered items
                return  query ? $scope.choicesA.filter( createFilterFor(query) ) : [];
            }


            function createFilterFor(query) {
                var lowercaseQuery = angular.lowercase(query);

                return function filterFn(item) {
                    // Check if the given item matches for the given query
                    var label = angular.lowercase(item.label);
                    return (label.indexOf(lowercaseQuery) === 0);
                };
            }
        })

        .controller('autoCpletZoneLivraisonController', function($scope, $cookies) {

            $scope.filteredListZ = [];
            $scope.isVisibleZ = {
                suggestions: false
            };

            $scope.filterItemsZ = function () {
                if($scope.minlength <= $scope.enteredtextZ.length) {
                    $scope.filteredListZ            = querySearch($scope.enteredtextZ);
                    $scope.isVisibleZ.suggestions   = $scope.filteredListZ.length > 0 ? true : false;
                }
                else {
                    $scope.isVisibleZ.suggestions   = false;
                }
            };


            $scope.selectItemZ = function (index) {
                $cookies.remove('uZoneLivraison');
                $scope.selectedZ                = $scope.choicesZ[index - 1];
                $scope.enteredtextZ             = $scope.selectedZ.label;
                $scope.isVisibleZ.suggestions   = false;
                $cookies.putObject('uZoneLivraison', $scope.selectedZ.index);
            };


            function querySearch (query) {
                // returns list of filtered items
                return  query ? $scope.choicesZ.filter( createFilterFor(query) ) : [];
            }


            function createFilterFor(query) {
                var lowercaseQuery = angular.lowercase(query);

                return function filterFn(item) {
                    // Check if the given item matches for the given query
                    var label = angular.lowercase(item.label);
                    return (label.indexOf(lowercaseQuery) === 0);
                };
            }
        })

        /*.controller('autoCompletLivreurController', function($scope, $cookies) {

            $scope.filteredListL = [];
            $scope.isVisibleL = {
                suggestions: false
            };

            $scope.filterItemsL = function () {
                if($scope.minlength <= $scope.enteredtextL.length) {
                    $scope.filteredListL            = querySearchL($scope.enteredtextR);
                    $scope.isVisibleL.suggestions   = $scope.filteredListL.length > 0 ? true : false;
                }
                else {
                    $scope.isVisibleL.suggestions   = false;
                }
            };


            $scope.selectItemL = function (index) {
                $cookies.remove('uLivreur');
                $scope.selectedL                = $scope.choicesL[index - 1];
                $scope.enteredtextL             = $scope.selectedL.label;
                $scope.isVisibleL.suggestions   = false;
                $cookies.putObject('uLivreur', $scope.selectedL.index);
            };


            function querySearchL (query) {
                // returns list of filtered items
                return  query ? $scope.choicesL.filter( createFilterForL(query) ) : [];
            }


            function createFilterForL(query) {
                var lowercaseQuery = angular.lowercase(query);

                return function filterFn(item) {
                    // Check if the given item matches for the given query
                    var label = angular.lowercase(item.label);
                    return (label.indexOf(lowercaseQuery) === 0);
                };
            }
        })*/


})();