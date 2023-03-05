(function() {

    'use strict';

    DiGital

        .controller('ServiceCourierEnvoiController', function ($rootScope, $scope, $location, $http, alertify, $state,
                                                          $timeout, $cookies, ROOT_URL, $log, $modal, DTOptionsBuilder,
                                                               DTColumnDefBuilder) {
            if($cookies.getObject('uData')){

                $scope.loadingPage  = true;
                $scope.api          = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token    = $cookies.getObject('jw_token');
                $scope.datas        = {};
                var _this 			= this;

                $scope.dtOptions = DTOptionsBuilder
                    .newOptions()
                    .withBootstrap("responsive",!0)
                    .withOption('order', [[1, 'asc']])
                    .withOption('lengthMenu', [20, 50, 150, 250, 300])
                    .withLanguage({
                        "sLengthMenu": 'Voir _MENU_ Enregistrements',
                        "sSearch": "Rechercher	&nbsp;:   ",
                        "sProcessing": "Traitement en cours...",
                        "sInfo": 'Trouvé : _TOTAL_ enregistrement(s)',
                        "sLoadingRecords": "Chargement en cours...",
                        "oPaginate": {
                            "sFirst": " Premier ",
                            "sPrevious": " Pr&eacute;c&eacute;dent ",
                            "sNext": " Suivant ",
                            "sLast": " Dernier ",
                            "sPage": "Page",
                            "sPageOf": "sur"
                        },
                        "oAria": {
                            "sSortAscending": ": activer pour trier la colonne par ordre croissant",
                            "sSortDescending": ": activer pour trier la colonne par ordre d&eacute;croissant"
                        }
                    })
                    .withPaginationType('full_numbers')
                    .withColumnFilter();

                $scope.dtColumnDefs = [
                    DTColumnDefBuilder.newColumnDef(0).notSortable(),
                    DTColumnDefBuilder.newColumnDef(9).notSortable()
                ];


                $scope.initialize = function () {

                    $scope.loadingPage = true;

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-courrier-envoye/"+$cookies.getObject('uData').profile.idDigital,

                        method: "GET",

                        async: true,

                        cache: false,

                        headers: {

                            'Content-Type': 'application/json',

                            'Authorization': 'Bearer ' + $scope.jwt_token,

                            'Accept': 'application/json',
                        }
                    })
                        .then(function(data){
                                $scope.loadingPage = false;
                                $scope.datas = data.data;
                            },
                            function(data, status, xhrStatus){
                                $scope.loadingPage = false;
                            });
                };
                $scope.initialize();


                $scope.openFrmDetails = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/services/service-courier/envoi-colis/detials-envoi-colis.html',
                        controller: 'InstanceDetailsEnvoi',
                        resolve: {
                            item: function () {
                                return item || null;
                            }
                        }
                    });

                    modalInstance.result.then(function (result) {
                        console.log('result: ' + item);
                    }, function () {
                        $scope.initialize();
                    });
                }


                $scope.printReceipt = function(mData) {
                    if(mData){
                        _this.imprimerDocument($scope.api+"/print-receipt", mData, "receipt_"+mData.numeroDevis+".pdf");
                    }
                }


                /**
                 * Ouverture/impression des 3 exemplaires
                 */
                this.imprimerDocument = function (urlImpression, data, fileName){

                    $scope.loading = true;

                    var ieEDGE = navigator.userAgent.match(/Edge/g);

                    var ie =  navigator.userAgent.match(/.NET/g);

                    var oldIE =  navigator.userAgent.match(/MSIE/g);

                    $http({

                        method : "POST",

                        url : $scope.api+"/"+urlImpression,

                        responseType: 'arraybuffer',

                        data : data

                    }).then(function successCallback(response) {

                        if(ie || oldIE || ieEDGE){

                            var blob = new window.Blob([response.data],{type: 'application/pdf'});

                            window.navigator.msSaveBlob(blob,fileName);

                            $scope.loading = false;

                        }
                        else{
                            var file = new Blob([response.data], {type: 'application/pdf'});

                            var fileURL = window.URL.createObjectURL(file);

                            var win = window.open(fileURL, '_blank');

                            if(win){
                                win.focus();
                            }
                            $scope.loading = false;
                        }

                    }, function errorCallback(response) {
                        $log.info("Service non disponible");
                        $scope.loading = false;
                    });
                }
            }
            else
                $state.go("404");
        })

        .controller('ServiceCourierRetirerController', function ($rootScope, $scope, $location, $http, alertify, $state,
                                                               $timeout, $cookies, ROOT_URL, $log, $modal, DTOptionsBuilder,
                                                               DTColumnDefBuilder) {
            if($cookies.getObject('uData')){

                $scope.loadingPage = true;
                $scope.api = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token = $cookies.getObject('jw_token');

                $scope.datas = {};

                $scope.dtOptions = DTOptionsBuilder
                    .newOptions()
                    .withBootstrap("responsive",!0)
                    .withOption('order', [[1, 'asc']])
                    .withOption('lengthMenu', [20, 50, 150, 250, 300])
                    .withLanguage({
                        "sLengthMenu": 'Voir _MENU_ Enregistrements',
                        "sSearch": "Rechercher	&nbsp;:   ",
                        "sProcessing": "Traitement en cours...",
                        "sInfo": 'Trouvé : _TOTAL_ enregistrement(s)',
                        "sLoadingRecords": "Chargement en cours...",
                        "oPaginate": {
                            "sFirst": " Premier ",
                            "sPrevious": " Pr&eacute;c&eacute;dent ",
                            "sNext": " Suivant ",
                            "sLast": " Dernier ",
                            "sPage": "Page",
                            "sPageOf": "sur"
                        },
                        "oAria": {
                            "sSortAscending": ": activer pour trier la colonne par ordre croissant",
                            "sSortDescending": ": activer pour trier la colonne par ordre d&eacute;croissant"
                        }
                    })
                    .withPaginationType('full_numbers')
                    .withColumnFilter();

                $scope.dtColumnDefs = [
                    DTColumnDefBuilder.newColumnDef(0).notSortable(),
                    DTColumnDefBuilder.newColumnDef(9).notSortable()
                ];


                $scope.initialize = function () {

                    $scope.loadingPage = true;

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-courrier-retire-par-cie/"+$cookies.getObject('uData').profile.idDigital,

                        method: "GET",

                        async: true,

                        cache: false,

                        headers: {

                            'Content-Type': 'application/json',

                            'Authorization': 'Bearer ' + $scope.jwt_token,

                            'Accept': 'application/json',
                        }
                    })
                        .then(function(data){
                                $scope.loadingPage = false;
                                $scope.datas = data.data;
                            },
                            function(data, status, xhrStatus){
                                $scope.loadingPage = false;
                            });
                };
                $scope.initialize();


                /*$scope.openFrmDetails = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/services/service-courier/envoi-colis/detials-envoi-colis.html',
                        controller: 'InstanceDetailsEnvoi',
                        resolve: {
                            item: function () {
                                return item || null;
                            }
                        }
                    });

                    modalInstance.result.then(function (result) {
                        console.log('result: ' + item);
                    }, function () {
                        $scope.initialize();
                    });
                }*/
            }
            else
                $state.go("404");
        })

        .controller('InstanceDetailsEnvoi', function($scope, $modalInstance, item, $http, alertify, $location,
                                                              ROOT_URL, $state, $cookies, $log) {

            if($cookies.getObject('uData')) {

                $scope.api          = $location.protocol() + "://" + $location.host() + ':' + $location.port() + "" + ROOT_URL.api;
                $scope.jwt_token    = $cookies.getObject('jw_token');
                $scope.items        = {};
                $scope.loadingPage  = true;

                $scope.fermer = function () {
                    $modalInstance.dismiss('cancel');
                };


                if (item !== null && item !== '') {

                    $scope.titleFrm = item.colisNumber;

                    $scope.allColis = function () {

                        $http({

                            dataType: 'jsonp',

                            url: $scope.api+"/find-all-colis-attached/"+item.id,

                            method: "GET",

                            async: true,

                            cache: false,

                            headers: {

                                'Content-Type': 'application/json',

                                'Authorization': 'Bearer ' + $scope.jwt_token,

                                'Accept': 'application/json',
                            }
                        })
                        .then(function(data){
                            $scope.loadingPage = false;
                            $scope.datas = data.data;
                            $scope.items = item;
                        },
                        function(data, status, xhrStatus){
                            $scope.loadingPage = false;
                        });
                    }();
                }
            }
            else{
                $state.go("404");
            }
        })

        .controller('autocompleteController', function($scope, $cookies) {

            $scope.filteredChoices = [];
            $scope.isVisible = {
                suggestions: false
            };

            $scope.filterItems = function () {
                if($scope.minlength <= $scope.enteredtext.length) {
                    $scope.filteredChoices = querySearch($scope.enteredtext);
                    $scope.isVisible.suggestions = $scope.filteredChoices.length > 0 ? true : false;
                }
                else {
                    $scope.isVisible.suggestions = false;
                }
            };


            /**
             * Takes one based index to save selected choice object
             */
            $scope.selectItem = function (index) {
                $cookies.remove('uChoice');
                $scope.selected = $scope.choices[index - 1];
                $scope.enteredtext = $scope.selected.label;
                $scope.isVisible.suggestions = false;
                $cookies.putObject('uChoice', $scope.enteredtext);
            };

            /**
             * Search for states... use $timeout to simulate
             * remote dataservice call.
             */
            function querySearch (query) {
                // returns list of filtered items
                return  query ? $scope.choices.filter( createFilterFor(query) ) : [];
            }

            /**
             * Create filter function for a query string
             */
            function createFilterFor(query) {
                var lowercaseQuery = angular.lowercase(query);

                return function filterFn(item) {
                    // Check if the given item matches for the given query
                    var label = angular.lowercase(item.label);
                    return (label.indexOf(lowercaseQuery) === 0);
                };
            }
        })


        .controller('autocomplete2Controller', function($scope) {

            $scope.filteredList = [];
            $scope.isVisible = {
                suggestions: false
            };

            $scope.filterItems = function () {
                if($scope.minlength <= $scope.enteredtext2.length) {
                    $scope.filteredList = querySearch($scope.enteredtext2);
                    $scope.isVisible.suggestions = $scope.filteredList.length > 0 ? true : false;
                }
                else {
                    $scope.isVisible.suggestions = false;
                }
            };


            /**
             * Takes one based index to save selected choice object
             */
            $scope.selectItem = function (index) {
                $scope.selected2 = $scope.choices[index - 1];
                $scope.enteredtext2 = $scope.selected2.label;
                $scope.isVisible.suggestions = false;
            };

            /**
             * Search for states... use $timeout to simulate
             * remote dataservice call.
             */
            function querySearch (query) {
                // returns list of filtered items
                return  query ? $scope.choices.filter( createFilterFor(query) ) : [];
            }

            /**
             * Create filter function for a query string
             */
            function createFilterFor(query) {
                var lowercaseQuery = angular.lowercase(query);

                return function filterFn(item) {
                    // Check if the given item matches for the given query
                    var label = angular.lowercase(item.label);
                    return (label.indexOf(lowercaseQuery) === 0);
                };
            }
        })


        .controller('ServiceInstanceEnvoiColis', function($scope, $http, alertify, $location, ROOT_URL, $state, $cookies, $log) {

            if($cookies.getObject('uData')){

                $scope.api              = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token        = $cookies.getObject('jw_token');

                $scope.loadingPage  = false;
                $scope.actived          = true;
                $scope.items            = {};
                $scope.items.gare       = $cookies.getObject('uData').profile.gareRoutiere;
                $scope.items.profile    = $cookies.getObject('uData').profile;
                $scope.gare             = $scope.items.gare.gareRoutiere;
                $scope.compagnie        = $scope.items.gare.compagnie.compagnie;
                $scope.jx               = {};
                $scope.cpte             = 0;
                $scope.nb               = {};
                $scope.nb.colis         = 0;


                $scope.villes = [
                    {index: 1, label: 'Abidjan'},
                    {index: 322, label: 'Adjamé'},
                    {index: 323, label: 'Koumassi'},
                    {index: 324, label: 'Marcory'},
                    {index: 325, label: 'Treichville'},
                    {index: 326, label: 'Port-Bouët'},
                    {index: 327, label: 'Yopougon'},
                    {index: 1, label: 'Abidjan'},
                    {index: 2, label: 'Abengourou'},
                    {index: 3, label: 'Aboisso'},
                    {index: 4, label: 'Abongoua'},
                    {index: 5, label: 'Adaou'},
                    {index: 6, label: 'Adiaké'},
                    {index: 7, label: 'Adjouan'},
                    {index: 8, label: 'Adzopé'},
                    {index: 9, label: 'Agbaou'},
                    {index: 10, label: 'Agboville'},
                    {index: 11, label: 'Agnibilékrou'},
                    {index: 12, label: 'Ahouanou'},
                    {index: 13, label: 'Ahoutoué'},
                    {index: 14, label: 'Akouédo'},
                    {index: 15, label: 'Akoupé'},
                    {index: 16, label: 'Alépé'},
                    {index: 17, label: 'Alounamouénou'},
                    {index: 18, label: 'Ananda (Daoukro)'},
                    {index: 19, label: 'Ananguié (Adzopé)'},
                    {index: 20, label: 'Annépé'},
                    {index: 21, label: 'Anyama'},
                    {index: 22, label: 'Arrah'},
                    {index: 23, label: 'Assaoufoué'},
                    {index: 24, label: 'Attiégouakro'},
                    {index: 25, label: 'Attoutou'},
                    {index: 26, label: 'Azaguié'},
                    {index: 27, label: 'Bacanda'},
                    {index: 28, label: 'Badikaha'},
                    {index: 29, label: 'Bako'},
                    {index: 30, label: 'Baléko'},
                    {index: 31, label: 'Bambalouma'},
                    {index: 32, label: 'Bandakagni-Sokoura'},
                    {index: 33, label: 'Bangolo'},
                    {index: 34, label: 'Bangoua'},
                    {index: 35, label: 'Banneu'},
                    {index: 36, label: 'Batéguédia II'},
                    {index: 37, label: 'Bazra-Nattis'},
                    {index: 38, label: 'Bécouéfin'},
                    {index: 39, label: 'Béoumi'},
                    {index: 40, label: 'Bettié'},
                    {index: 41, label: 'Biankouma'},
                    {index: 42, label: 'Biéby'},
                    {index: 43, label: 'Bin-Houyé'},
                    {index: 44, label: 'Blapleu'},
                    {index: 45, label: 'Bléniméouin'},
                    {index: 46, label: 'Blességué'},
                    {index: 47, label: 'Bloléquin'},
                    {index: 48, label: 'Boahia'},
                    {index: 49, label: 'Bocanda'},
                    {index: 50, label: 'Bogouiné'},
                    {index: 51, label: 'Boli'},
                    {index: 52, label: 'Bondo'},
                    {index: 53, label: 'Bongo'},
                    {index: 54, label: 'Bongouanou'},
                    {index: 55, label: 'Bonoua'},
                    {index: 56, label: 'Boromba'},
                    {index: 57, label: 'Botro'},
                    {index: 58, label: 'Bouaflé'},
                    {index: 59, label: 'Bouandougou'},
                    {index: 60, label: 'Bougousso'},
                    {index: 61, label: 'Bouna'},
                    {index: 62, label: 'Boundiali'},
                    {index: 63, label: 'Brofodoumé'},
                    {index: 64, label: 'Céchi'},
                    {index: 65, label: 'Dabéko'},
                    {index: 66, label: 'Dabou'},
                    {index: 67, label: 'Dabouyo'},
                    {index: 68, label: 'Dah-Zagna'},
                    {index: 69, label: 'Dakpadou'},
                    {index: 70, label: 'Daleu'},
                    {index: 71, label: 'Daloa'},
                    {index: 72, label: 'Danané'},
                    {index: 73, label: 'Danguira'},
                    {index: 74, label: 'Daoukro'},
                    {index: 75, label: 'Diabo'},
                    {index: 76, label: 'Diamarakro'},
                    {index: 77, label: 'Diangobo (Yakassé-Attobrou)'},
                    {index: 78, label: 'Diawala'},
                    {index: 79, label: 'Diboké'},
                    {index: 80, label: 'Didiévi'},
                    {index: 81, label: 'Diéouzon'},
                    {index: 82, label: 'Digbeugnoa'},
                    {index: 83, label: 'Dignago'},
                    {index: 84, label: 'Dikouehipalegnoa'},
                    {index: 85, label: 'Dimbokro'},
                    {index: 86, label: 'Diogo'},
                    {index: 87, label: 'Dioulatiédougou'},
                    {index: 88, label: 'Divo'},
                    {index: 89, label: 'Djouroutou'},
                    {index: 90, label: 'Doba'},
                    {index: 91, label: 'Dogbo'},
                    {index: 92, label: 'Doké'},
                    {index: 93, label: 'Domaboué'},
                    {index: 94, label: 'Domangbeu'},
                    {index: 95, label: 'Douasso'},
                    {index: 96, label: 'Doubé'},
                    {index: 97, label: 'Doudoukou'},
                    {index: 98, label: 'Duékoué'},
                    {index: 99, label: 'Ebounou'},
                    {index: 100, label: 'Elima'},
                    {index: 101, label: 'Ery-Macouguié'},
                    {index: 102, label: 'Fadiadougou'},
                    {index: 103, label: 'Fahandougou'},
                    {index: 104, label: 'Fahani'},
                    {index: 105, label: 'Fakaha'},
                    {index: 106, label: 'Fala'},
                    {index: 107, label: 'Famienkro'},
                    {index: 108, label: 'Faradiani'},
                    {index: 109, label: 'Farandougou'},
                    {index: 110, label: 'Ferkessédougou'},
                    {index: 111, label: 'Fodio'},
                    {index: 112, label: 'Fonondara'},
                    {index: 113, label: 'Fresco'},
                    {index: 114, label: 'Gabiadji'},
                    {index: 115, label: 'Gagnoa'},
                    {index: 116, label: 'Ganaoni'},
                    {index: 117, label: 'Gbambiasso'},
                    {index: 118, label: 'Gbangbégouiné'},
                    {index: 119, label: 'Gbangbégouiné-Yati'},
                    {index: 120, label: 'Gbékékro'},
                    {index: 121, label: 'Gbéléban'},
                    {index: 122, label: 'Gbémou'},
                    {index: 123, label: 'Gbogui'},
                    {index: 124, label: 'Gbon'},
                    {index: 125, label: 'Gbongaha'},
                    {index: 126, label: 'Gnaliepa'},
                    {index: 127, label: 'Gnangnon'},
                    {index: 128, label: 'Gohouo-Zagna'},
                    {index: 129, label: 'Gomon'},
                    {index: 130, label: 'Gonaté'},
                    {index: 131, label: 'Gouessesso'},
                    {index: 132, label: 'Gouiné'},
                    {index: 133, label: 'Goulia'},
                    {index: 134, label: 'Grabo'},
                    {index: 135, label: 'Grand-Bassam'},
                    {index: 136, label: 'Grand-Béréby'},
                    {index: 137, label: 'Grand-Lahou'},
                    {index: 138, label: 'Grand-Morié'},
                    {index: 139, label: 'Grand-Zattry'},
                    {index: 140, label: 'Guessabo'},
                    {index: 141, label: 'Guéyo'},
                    {index: 142, label: 'Guiendé'},
                    {index: 143, label: 'Guiglo'},
                    {index: 144, label: 'Guinglo-Tahouaké'},
                    {index: 145, label: 'Issia'},
                    {index: 146, label: 'Jacqueville'},
                    {index: 147, label: 'Kahin-Zarabaon'},
                    {index: 148, label: 'Kanakono'},
                    {index: 149, label: 'Kaniéné'},
                    {index: 150, label: 'Kanitélégué'},
                    {index: 151, label: 'Kanoroba'},
                    {index: 152, label: 'Kantélégué'},
                    {index: 153, label: 'Kanzra'},
                    {index: 154, label: 'Kaouara'},
                    {index: 155, label: 'Karakoro'},
                    {index: 156, label: 'Karakpo'},
                    {index: 157, label: 'Kasséré'},
                    {index: 158, label: 'Katiali'},
                    {index: 159, label: 'Katiéré'},
                    {index: 160, label: 'Katiola'},
                    {index: 161, label: 'Kimbirila-Sud'},
                    {index: 162, label: 'Koboko'},
                    {index: 163, label: 'Kodiokofi'},
                    {index: 164, label: 'Kofiplé'},
                    {index: 165, label: 'Kolia'},
                    {index: 166, label: 'Kong'},
                    {index: 167, label: 'Kongasso'},
                    {index: 168, label: 'Koni'},
                    {index: 169, label: 'Konolo'},
                    {index: 170, label: 'Korhogo'},
                    {index: 171, label: 'Koro'},
                    {index: 172, label: 'Kossou'},
                    {index: 173, label: 'Kouakro'},
                    {index: 174, label: 'Kouan-Houle'},
                    {index: 175, label: 'Kouassi-Blékro'},
                    {index: 176, label: 'Koukourandoumi'},
                    {index: 177, label: 'Kounoumon'},
                    {index: 178, label: 'Kouto'},
                    {index: 179, label: 'Kpata'},
                    {index: 180, label: 'Lahou-Kpanda'},
                    {index: 181, label: 'Lakota'},
                    {index: 182, label: 'Languibonou'},
                    {index: 183, label: 'Lataha'},
                    {index: 184, label: 'Liliyo'},
                    {index: 185, label: 'Lodala'},
                    {index: 186, label: 'Logoualé'},
                    {index: 187, label: 'Logouhi'},
                    {index: 188, label: 'Lohouré'},
                    {index: 189, label: 'Lokoligou'},
                    {index: 190, label: 'Lolobo (Yamoussoukro)'},
                    {index: 191, label: 'Lomokankro'},
                    {index: 192, label: 'Loplé'},
                    {index: 193, label: 'Lossingué'},
                    {index: 194, label: 'Lotono'},
                    {index: 195, label: 'Loupala'},
                    {index: 196, label: 'Loupougo'},
                    {index: 197, label: 'Loviguié'},
                    {index: 198, label: "M'bahiakro"},
                    {index: 199, label: "N'Dara"},
                    {index: 200, label: "N'douci"},
                    {index: 201, label: 'Nafana (Prikro)'},
                    {index: 202, label: 'Nagou'},
                    {index: 203, label: 'Nahio'},
                    {index: 204, label: 'Nandala'},
                    {index: 205, label: 'Nangbolodougou'},
                    {index: 206, label: 'Nassian'},
                    {index: 207, label: 'Natio'},
                    {index: 208, label: "N'Gokro"},
                    {index: 209, label: 'Niagbrahio'},
                    {index: 210, label: 'Niakaramandougou'},
                    {index: 211, label: 'Niambézaria'},
                    {index: 212, label: 'Niandono'},
                    {index: 213, label: 'Niangboué'},
                    {index: 214, label: 'Niangboué rivière'},
                    {index: 215, label: 'Nianzongo'},
                    {index: 216, label: 'Niellé'},
                    {index: 217, label: 'Niempurgué'},
                    {index: 218, label: 'Nigui-saff'},
                    {index: 219, label: 'Ninioro'},
                    {index: 220, label: 'Niofoin'},
                    {index: 221, label: 'Niorouhio'},
                    {index: 222, label: 'Nitiadougou'},
                    {index: 223, label: 'Nodiahan'},
                    {index: 224, label: 'Nofou'},
                    {index: 225, label: 'Nomparadougou'},
                    {index: 226, label: 'Nondara'},
                    {index: 227, label: 'Noonlara'},
                    {index: 228, label: 'Nouamou'},
                    {index: 229, label: 'Odienné'},
                    {index: 230, label: 'Olodio'},
                    {index: 231, label: 'Oress-Krobou'},
                    {index: 232, label: 'Ouangolodougou'},
                    {index: 233, label: 'Ouaragahio'},
                    {index: 234, label: 'Ouarapa'},
                    {index: 235, label: 'Ouattaradougou'},
                    {index: 236, label: 'Ouazomon'},
                    {index: 237, label: 'Ouéllé'},
                    {index: 238, label: 'Ouendé-Kouassikro'},
                    {index: 239, label: 'Oumé'},
                    {index: 240, label: 'Oupoyo'},
                    {index: 241, label: 'Ouyably-Gnondrou'},
                    {index: 242, label: 'Pacobo'},
                    {index: 243, label: 'Panadougou'},
                    {index: 244, label: 'Pelezi'},
                    {index: 245, label: 'Pinhou'},
                    {index: 246, label: 'Podiagouine'},
                    {index: 247, label: 'Ponadongou'},
                    {index: 248, label: 'Poniakélé'},
                    {index: 249, label: 'Ponondougou'},
                    {index: 250, label: 'Portio'},
                    {index: 251, label: 'Poundiou'},
                    {index: 252, label: 'Prikro'},
                    {index: 253, label: 'Rubino'},
                    {index: 254, label: 'Sakassou'},
                    {index: 255, label: 'Samatiguila'},
                    {index: 256, label: 'San-Pédro'},
                    {index: 257, label: 'Sandougou-Soba'},
                    {index: 258, label: 'Santa (Biankouma)'},
                    {index: 259, label: 'Sassandra'},
                    {index: 260, label: 'Segana'},
                    {index: 261, label: 'Séguéla'},
                    {index: 262, label: 'Séguelon'},
                    {index: 263, label: 'Seleho'},
                    {index: 264, label: 'Sianhala'},
                    {index: 265, label: 'Siempurgo'},
                    {index: 266, label: 'Sikensi'},
                    {index: 267, label: 'Sinématiali'},
                    {index: 268, label: 'Sinfra'},
                    {index: 269, label: 'Sirasso'},
                    {index: 270, label: 'Sodalako'},
                    {index: 271, label: 'Sokoro'},
                    {index: 272, label: 'Songon'},
                    {index: 273, label: 'Soubré'},
                    {index: 274, label: 'Tabayo 1'},
                    {index: 275, label: 'Taboitien'},
                    {index: 276, label: 'Tabou'},
                    {index: 277, label: 'Tafiré'},
                    {index: 278, label: 'Tahiraguhé'},
                    {index: 279, label: 'Talaho'},
                    {index: 280, label: 'Teapleu'},
                    {index: 281, label: 'Ténélogo'},
                    {index: 282, label: 'Tengréla (ville)'},
                    {index: 283, label: 'Tiagba'},
                    {index: 284, label: 'Tiapoum'},
                    {index: 285, label: 'Tiassalé'},
                    {index: 286, label: 'Tie Ndiékro'},
                    {index: 287, label: 'Tiébissou'},
                    {index: 288, label: 'Tiédio'},
                    {index: 289, label: 'Tiémé'},
                    {index: 290, label: 'Tienko'},
                    {index: 291, label: 'Tinhou'},
                    {index: 292, label: 'Togoniéré'},
                    {index: 293, label: 'Tonla'},
                    {index: 294, label: 'Tortiya'},
                    {index: 295, label: 'Tougbo'},
                    {index: 296, label: 'Toulepleu'},
                    {index: 297, label: 'Toumo (Boundiali)'},
                    {index: 298, label: 'Toumodi'},
                    {index: 299, label: 'Toumoukoro'},
                    {index: 300, label: 'Tounvré'},
                    {index: 301, label: 'Vavoua'},
                    {index: 302, label: 'Voueboufla'},
                    {index: 303, label: 'Waraniéné'},
                    {index: 304, label: 'Womon'},
                    {index: 305, label: 'Yakassé-Attobrou'},
                    {index: 306, label: 'Yakassé-Mé'},
                    {index: 307, label: 'Yama (Boundiali)'},
                    {index: 308, label: 'Yamoussoukro'},
                    {index: 309, label: 'Yaou'},
                    {index: 310, label: 'Yapleu'},
                    {index: 311, label: 'Yorodougou'},
                    {index: 312, label: 'Zéo'},
                    {index: 313, label: 'Ziasso'},
                    {index: 314, label: 'Ziédougou'},
                    {index: 315, label: 'Zonneu'},
                    {index: 316, label: 'ZOU'},
                    {index: 317, label: 'Zouan-Hounien'},
                    {index: 318, label: 'Zoukougbeu'},
                    {index: 319, label: 'Zuénoula'},
                    {index: 320, label: 'BOUAKÉ'},
                ];
                $scope.text2 = '';
                $scope.minlength = 1;
                $scope.selected = {};

                $scope.moisList = [
                    {
                        key : "01",
                        value : "JANVIER",
                    },
                    {
                        key : "02", value : "FEVRIER"
                    },
                    {
                        key : "03", value : "MARS"
                    },
                    {
                        key : "04", value : "AVRIL"
                    },
                    {
                        key : "05", value : "MAI"
                    },
                    {
                        key : "06", value : "JUIN"
                    },
                    {
                        key : "07", value : "JUILLET"
                    },
                    {
                        key : "08", value : "AOUT"
                    },
                    {
                        key : "09", value : "SEPTEMBRE"
                    },
                    {
                        key : "10", value : "OCTOBRE"
                    },
                    {
                        key : "11", value : "NOVEMBRE"
                    },
                    {
                        key : "12", value : "DECEMBRE"
                    }
                ];


                $scope.natureColisList = [
                    {
                        value : "AUTRES"
                    },
                    {
                        value : "ARGENT / FINANCES"
                    },
                    {
                        value : "APPAREILS ÉLECTRONIQUES"
                    },
                    {
                        value : "ELECTROMENAGÉS"
                    },
                    {
                        value : "DOCUMENTS"
                    },
                    {
                        value : "PIÈCES MECANIQUES"
                    },
                    {
                        value : "PRODUITS DE PREMIER NÉCÉSSITÉ"
                    },
                    {
                        value : "PRODUITS COSMÉTIQUES"
                    },
                    {
                        value : "PRODUITS AGRICOLE"
                    },
                    {
                        value : "PRODUITS PHARMACEUTIQUES"
                    },
                    {
                        value : "PRODUITS MÉDICAUX"
                    },
                    {
                        value : "MATÉRIEL"
                    }
                ];


                $scope.qtionFraisList = [
                    {
                        key : 1,
                        value : "OUI"
                    },
                    {
                        key : 2,
                        value : "RETRAIT DU COLIS"
                    }
                ];


                $scope.typeEnvoiList = [
                    {
                        key : "EXPRESS",
                        value : "COURRIER EXPRESS"
                    },
                    {
                        key : "NORMAL",
                        value : "COURRIER NORMAL"
                    }
                ];


                $scope.checkingField = function (obj) {
                    $scope.actived = obj ? parseInt(obj) !== 1 : true;

                    if($scope.items.valeurColis && !$scope.actived){
                        $scope.items.fraisEnvoi = $scope.items.valeurColis * 0.1;
                    }
                    else if($scope.items.valeurColis && $scope.actived){
                        $scope.items.fraisEnvoi = 0;
                    }
                };


                //Ajout des colis
                $scope.colis = [];
                $scope.addColis= function () {

                    $scope.cpte ++;

                    $scope.jx.id = $scope.cpte;

                    $scope.colis.push($scope.jx);

                    $scope.nb.colis = $scope.colis.length;

                    $scope.jx = {};
                }



                $scope.suppression = function(item){
                    $scope.colis.splice( $scope.colis.indexOf(item), 1);
                }

                $scope.goBack = function (route) {
                    $state.go(route);
                }

                $scope.save = function () {

                    $scope.loadingPage  = true;
                    $scope.items.frais = $scope.items.qtionFrais === "1" ? "FRAIS PAYER":"AU RETRAIT DU COLIS";

                    $scope.items.colis              = $scope.colis;
                    $scope.items.villeExpeditrice   = $scope.selected.label;
                    $scope.items.villeDestinatrice  = $scope.selected2.label;


                    $http({

                        url: $scope.api + '/save-envoi-de-colis',

                        method: "POST",

                        data: angular.toJson($scope.items),

                        async: true,

                        cache: false,

                        headers: {

                            'Content-Type': 'application/json',

                            'Authorization': 'Bearer ' + $scope.jwt_token,

                            'Accept': 'application/json',
                        }
                    })
                    .then(function (data) {

                        $scope.loadingPage = false;

                        if (data.data.information.requestCode === 201) {

                            alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : L'enregistrement du colis effectu&eacute; avec succ&egrave;s. Une notification sms a &eacute;t&eacute; envoy&eacute; aux diff&eacute;rents acteurs.",function(event){

                                if(event) {

                                    $state.go("mainpage.service-courier.list-courier-envoi");
                                }
                            });
                        }

                        else if (data.data.information.requestCode === 409) {
                            alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                        }

                        else {
                            alertify.alert("<strong>Di-Gital web</strong> : Impossible de traiter votre requête.");
                        }
                    }, function (data, status, headers, config) {
                        $scope.loadingPage = false;
                        alertify.alert("<strong>Di-Gital web</strong> : " + data);
                    });
                }

            }
            else{
                $state.go("404");
            }
        })


        .controller('ServiceInstanceRetraitColis', function($scope, $http, alertify, $location, ROOT_URL, $state,
                                                            $cookies, $log, $modal, DTOptionsBuilder,
                                                            DTColumnDefBuilder) {
            if($cookies.getObject('uData')) {

                $scope.api          = $location.protocol() + "://" + $location.host() + ':' + $location.port() + "" + ROOT_URL.api;
                $scope.jwt_token    = $cookies.getObject('jw_token');
                $scope.donnees      = {};
                $scope.launch       = 0;
                $scope.loadingPage  = false;

                $scope.dtOptions = DTOptionsBuilder
                    .newOptions()
                    .withBootstrap("responsive",!0)
                    .withOption('order', [[1, 'asc']])
                    .withOption('lengthMenu', [20, 50, 150, 250, 300])
                    .withLanguage({
                        "sLengthMenu": 'Voir _MENU_ Enregistrements',
                        "sSearch": "Rechercher	&nbsp;:   ",
                        "sProcessing": "Traitement en cours...",
                        "sInfo": 'Trouv&eacute; : _TOTAL_ enregistrement(s)',
                        "sLoadingRecords": "Chargement en cours...",
                        "oPaginate": {
                            "sFirst": " Premier ",
                            "sPrevious": " Pr&eacute;c&eacute;dent ",
                            "sNext": " Suivant ",
                            "sLast": " Dernier ",
                            "sPage": "Page",
                            "sPageOf": "sur"
                        },
                        "oAria": {
                            "sSortAscending": ": activer pour trier la colonne par ordre croissant",
                            "sSortDescending": ": activer pour trier la colonne par ordre d&eacute;croissant"
                        }
                    })
                    .withPaginationType('full_numbers')
                    .withColumnFilter();

                $scope.dtColumnDefs = [
                    DTColumnDefBuilder.newColumnDef(0).notSortable(),
                    DTColumnDefBuilder.newColumnDef(7).notSortable()
                ];

                $scope.retrait = {};

                $scope.tab = 1;

                $scope.setTab = function(newTab){
                    $scope.retrait  = {};
                    $scope.refColis = null;
                    $scope.donnees  = {};
                    $scope.tab = newTab;
                };

                $scope.isSet = function(tabNum){
                    return $scope.tab === tabNum;
                };


                $scope.search = function(position) {

                    if(position === "A"){

                        $scope.retrait.reference = $scope.retrait.reference.substring(0,2)+"/"+$scope.retrait.reference.substring(2,3)+"-"+$scope.retrait.reference.substring(3,7);

                        $scope.launch = 1;
                    }
                    else{

                        if($scope.retrait.nomDest === undefined && $scope.retrait.telDest === undefined && $scope.refColis === undefined){

                            $scope.launch = 0;

                            alertify.alert("<strong>Di-Gital web</strong> : Veuillez renseigner au moins un champs dans la partie recherche avanc&eacute;e.");
                        }
                        else{

                            //$log.info($scope.refColis)

                            if($scope.refColis !== null ){

                                $scope.retrait.reference = $scope.refColis.substring(0,2)+"/"+$scope.refColis.substring(2,3)+"-"+$scope.refColis.substring(3,7);
                            }

                            $scope.launch = 1;
                        }
                    }


                    if($scope.launch === 1){

                        $scope.loadingPage  = true;

                        $http({

                            url: $scope.api + '/recherche-de-colis',

                            method: "POST",

                            data: angular.toJson($scope.retrait),

                            async: true,

                            cache: false,

                            headers: {

                                'Content-Type': 'application/json',

                                'Authorization': 'Bearer ' + $scope.jwt_token,

                                'Accept': 'application/json',
                            }
                        })

                        .then(function (data) {
                            $scope.loadingPage = false;
                            $scope.donnees = data.data;

                            $log.info($scope.donnees);

                        }, function (data, status, headers, config) {
                            $scope.loadingPage = false;
                            $scope.donnees  = {};
                            alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                        });
                    }
                }


                $scope.listSmsEnvoye = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/services/service-courier/retrait-colis/list-sms-envoye-colis.html',
                        controller: 'ListSmsEnvoyeCtrl',
                        resolve: {
                            item: function () {
                                return item || null;
                            }
                        }
                    });

                    modalInstance.result.then(function (result) {
                        console.log('result: ' + item);
                        console.log('result: ' + result);
                    }, function (result) {
                        console.log('result: ' + result);
                    });
                }

                
                $scope.validateColis = function(event, item) {

                    alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> : Validez-vous le retrait du colis au code "+item.refColis+" ?",function(event){

                        if(event) {

                            var modalInstance = $modal.open({

                                templateUrl: ROOT_URL.absolute + '/services/service-courier/retrait-colis/valide-retrait-colis.html',

                                controller: 'InstanceValidateColisCtrl',

                                resolve: {
                                    item: function() { return item || null; }
                                }
                            });

                            modalInstance.result.then(function (result) {
                                $scope.retrait  = {};
                                $scope.refColis = null;
                                $scope.donnees  = {};
                            }, function (result) {
                                console.log('result: ' + result);
                            });
                        }
                    });
                };
            }
            else{
                $state.go("login");
            }
        })

        .controller('ListSmsEnvoyeCtrl', function($scope, $modalInstance, item, $log, $cookies, alertify) {

            if($cookies.getObject('uData')){

                $scope.listSms = {};

                if (item !== null && item !=='') {

                    $scope.titleFrm = item.refColis;

                    $scope.listSms = item;
                }

                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };


                $scope.voirMessage = function(dat) {
                    alertify.alert("<strong>MESSAGE DE TYPE "+dat.typeMessage.typemessgae+"</strong> : <br> <p>"+dat.corpsMessage+"</p>");
                }
            }
            else{
                $state.go("login");
            }
        })


        .controller('InstanceValidateColisCtrl', function($scope, $modalInstance, item, $http, alertify, $location,
                                                          ROOT_URL, $state, $cookies, $log) {

            if($cookies.getObject('uData')){

                $scope.nomPrenoms = $cookies.getObject('uData').profile.nomPrenoms;


                $scope.items      = {};
                $scope.obj        = {};

                if (item !== null && item !=='') {

                    $scope.titleFrm = "Retrait colis - Ref : "+item.refColis;

                    $scope.items = item;

                    if(item.montantFrais === 0){

                        $scope.totalPayer = parseInt(item.valeurColis) * 0.1 + parseInt(item.suiviSms);
                    }
                    else{
                        $scope.totalPayer = item.totalPaye;
                    }

                    $scope.api 			= $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                    $scope.style 		= "btn-success";
                    $scope.nomBtn 		= "Valider colis";

                }

                $scope.validerRetrait = function () {

                    $scope.obj.reference    = item.refColis;
                    $scope.obj.nomDest      = item.valideur;
                    $scope.obj.telDest      = item.phoneDestinatire;
                    $scope.loadingPage      = true;

                    $http({

                        url: $scope.api + '/valider-retrait-du-colis',

                        method: "PUT",

                        data: angular.toJson($scope.obj),

                        async: true,

                        cache: false,

                        headers: {

                            'Content-Type': 'application/json',

                            'Authorization': 'Bearer ' + $cookies.getObject('jw_token'),

                            'Accept': 'application/json',
                        }
                    })

                    .then(function (data) {

                        $scope.loadingPage = false;

                        if (data.data.information.requestCode === 201) {

                            alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : Le retrait du colis ou courrier n°"+item.refColis+" a été retir&eacute; avec succes. Une notifications a &eacute;t&eacute; envoy&eacute;e",function(event){

                                if(event) {

                                    $modalInstance.dismiss();
                                }
                            });

                        } else if (data.data.information.requestCode === 400) {

                            alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                        } else {
                            alertify.alert("<strong>Di-Gital web</strong> : Impossible de traiter votre requ&ecirc;te.");
                        }

                    }, function (data, status, headers, config) {

                        $scope.loadingPage = false;
                        alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                    });
                }


                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };
            }
            else{
                $state.go("login");
            }
        })

        .controller('MenuServiceCourierController', function($scope, $cookies) {

            if($cookies.getObject('uData')){

                $scope.loadingPage = false;
                $scope.viewMenu = true;
            }
            else
                $state.go("404");
        });
})();