(function() {

    'use strict';

    DiGital

        .controller('TrackingCourrierController', function($scope, $cookies, $log, $http, $location, ROOT_URL, alertify, $state) {

            if($cookies.getObject('uData')){

                $scope.api          = $location.protocol() + "://" + $location.host() + ':' + $location.port() + "" + ROOT_URL.api;
                $scope.jwt_token    = $cookies.getObject('jw_token');
                $scope.retrait      = {};
                $scope.items        = {};
                $scope.historique   = false;
                $scope.loadingPage  = false;
                $scope.launch       = 0;
                $scope.date       = new Date();


                $scope.search = function() {

                    if($scope.retrait.reference !== undefined){

                        $scope.retrait.reference = $scope.retrait.reference.substring(0,2)+"/"+$scope.retrait.reference.substring(2,3)+"-"+$scope.retrait.reference.substring(3,7);

                        $scope.launch = 1;
                    }
                    else{
                        $scope.loadingPage  = false;
                        $scope.historique   = false;
                        $scope.retrait      = {};
                        $scope.launch       = 0;
                    }


                    if($scope.launch === 1){

                        //$log.info("Ok bon, je lance la requête");

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
                            $scope.loadingPage  = false;
                            $scope.historique   = data.data.find === 1;
                            $scope.items        = data.data.list[0];
                            //$log.info($scope.items);
                            //$log.info($scope.items.suivi.id);

                            $http({

                                dataType: 'jsonp',

                                url: $scope.api+"/find-all-suivi-demande-disponible/"+$scope.items.suivi.id,

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
                                $scope.listeSuivi = data.data;
                            },
                            function(data, status, xhrStatus){
                                $scope.loadingPage = false;
                            });

                        }, function (data, status, headers, config) {
                            $scope.loadingPage  = false;
                            $scope.historique   = false;
                            $scope.donnees      = {};
                            alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                        });

                        //$log.info($scope.items.suivi.id);

                        /**
                         * Charge la liste des suivis fonction du dernier suivi
                         */
                        /*$http({
                            method : 'GET',
                            url : 'params/marques-moto.js',
                        })
                        .success(function(data){
                            $scope.listemarqueMoto = data;
                        });*/
                    }
                }


                $scope.gets = {};


                $scope.saveAction = function(donnees) {

                    $log.info(donnees);

                    $scope.loadingPage              = true;
                    $scope.gets.reference           = donnees.refColis;
                    $scope.gets.tarckingSuivant     = parseInt(donnees.tarckingSuivant);

                    $http({

                        url: $scope.api + '/mise-a-jour-tracking-du-colis',

                        method: "PUT",

                        data: angular.toJson($scope.gets),

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

                            $scope.historique   = false;
                            $scope.retrait      = {};
                            $scope.launch       = 0;

                            alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : La mise &agrave; du tracking du colis a &eacute;t&eacute; effectu&eacute; avec succ&egrave;s. un sms information a &eacute;t&eacute; envoy&eacute; &agrave; l'expéditeur.",function(event){

                                if(event) {
                                    $scope.historique   = false;
                                    $scope.retrait      = {};
                                    $scope.launch       = 0;
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
            else
                $state.go("404");
        })

        .controller('TrackingColisJournalierController', function($scope, $cookies, $log, $http, $location, ROOT_URL, alertify, $state) {

            if($cookies.getObject('uData')){

                $scope.api          = $location.protocol() + "://" + $location.host() + ':' + $location.port() + "" + ROOT_URL.api;
                $scope.jwt_token    = $cookies.getObject('jw_token');
                $scope.retrait      = {};
                $scope.items        = {};
                $scope.historique   = false;
                $scope.loadingPage  = false;
                $scope.launch       = 0;
                $scope.date         = new Date();



                $scope.search = function() {

                    if($scope.retrait.reference !== undefined){

                        $scope.retrait.reference = $scope.retrait.reference.substring(0,2).toUpperCase()+"-"+$scope.retrait.reference.substring(2,8);

                        $scope.launch = 1;
                    }
                    else{

                        $scope.loadingPage  = false;

                        $scope.historique   = false;

                        $scope.retrait      = {};

                        $scope.launch       = 0;
                    }

                    if($scope.launch === 1){

                        $scope.loadingPage  = true;

                        $http({

                            url: $scope.api + '/recherche-de-fiche-recuperation-colis',

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

                            $scope.loadingPage  = false;

                            if(data.status === 500){

                                alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                            }
                            else{

                                $scope.historique   = true;

                                $scope.items        = data.data;

                                $log.info($scope.items);
                            }

                        }, function (data, status, headers, config) {
                            $scope.loadingPage  = false;
                            $scope.historique   = false;
                            $scope.donnees      = {};
                            alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                        });
                    }
                }


                $scope.goBackTo1 = function () {
                    $scope.retrait      = {};
                    $scope.items        = {};
                    $scope.historique   = false;
                    $scope.launch       = 0;
                }
            }
            else
                $state.go("404");
        })

        .controller('AcheminementSuiviController', function($scope, $cookies, $log, $http, $location, ROOT_URL, alertify, $state) {

            $log.info("Acheminement Suivi Controller")
            if($cookies.getObject('uData')){

                $scope.api          = $location.protocol() + "://" + $location.host() + ':' + $location.port() + "" + ROOT_URL.api;
                $scope.jwt_token    = $cookies.getObject('jw_token');
                $scope.colis        = {};
                $scope.items        = {};
                $scope.result       = {};
                $scope.historique   = false;
                $scope.loadingPage  = false;
                $scope.launch       = 0;
                $scope.date         = new Date();


                $scope.getSearchColis = function () {

                    $scope.loadingPage  = true;

                    if($scope.colis.refColis === undefined && $scope.colis.ordre === undefined){

                        $scope.launch = 0;

                        alertify.alert("<strong>Di-Gital web</strong> : Veuillez renseigner au moins un champs dans la partie recherche avanc&eacute;e.");
                    }
                    else if($scope.colis.refColis !== undefined && $scope.colis.ordre === undefined){

                        $scope.items.reference = $scope.colis.refColis.substring(0,2).toUpperCase()+"-"+$scope.colis.refColis.substring(2,8)+"-"+$scope.colis.refColis.substring(8,11);

                        $scope.launch = 1;

                    }
                    else if($scope.colis.refColis === undefined && $scope.colis.ordre !== undefined){

                        $scope.items.ordre = parseInt($scope.colis.ordre);

                        $scope.launch = 1;

                    }
                    else if($scope.colis.refColis !== undefined && $scope.colis.ordre !== undefined){

                        $scope.items.ordre = parseInt($scope.colis.ordre);

                        $scope.items.reference = $scope.colis.refColis.substring(0,2).toUpperCase()+"-"+$scope.colis.refColis.substring(2,8)+"-"+$scope.colis.refColis.substring(8,11);

                        $scope.launch = 1;

                    }

                    //$log.info($scope.colis);

                    if($scope.launch === 1){

                        $scope.loadingPage  = true;

                        $http({

                            url: $scope.api + '/recherche-de-colis-pour-acheminement',

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

                            $scope.loadingPage  = false;

                            //$log.info(data.status)

                            if(data.status === 500){

                                alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                            }
                            else{

                                $scope.historique   = true;

                                $scope.result       = data.data;

                                $scope.colis        = {};
                            }

                        }, function (data, status, headers, config) {

                            $scope.loadingPage  = false;

                            $scope.historique   = false;

                            alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                        });
                    }
                }


                $scope.goBackTo = function (){
                    $scope.colis        = {};
                    $scope.result       = {};
                    $scope.historique   = false;
                }

            }
            else
                $state.go("404");
        })



        .controller('AcheminementFinaliserController', function($scope, $cookies, $log, $http, $location, ROOT_URL, alertify,
                                                            $state, DTColumnDefBuilder, DTOptionsBuilder) {

            if($cookies.getObject('uData')){

                $log.info("Acheminement Finaliser Controller")

                $scope.api          = $location.protocol() + "://" + $location.host() + ':' + $location.port() + "" + ROOT_URL.api;
                $scope.jwt_token    = $cookies.getObject('jw_token');
                $scope.loadingPage  = false;
                $scope.datas        = {};
                $scope.objets       = {};

                $scope.dtOptions = DTOptionsBuilder
                    .newOptions()
                    .withBootstrap("responsive",!0)
                    .withOption('order', [[0, 'asc']])
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
                    DTColumnDefBuilder.newColumnDef(6).notSortable()
                ];



                $scope.findall = function () {

                    $scope.loadingPage = true;

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-livraison-en-cours-by-profile-with-notification/"+$cookies.getObject('uData').profile.idDigital,

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
                $scope.findall();

                $scope.msgRemmerciement = function (id, item) {

                    if(item != null){

                        if(item.statutLivraison !== 'EN_COURS'){

                            alertify.alert("<strong>Di-Gital web</strong> : Tous les messages n&eacute;c&eacute;ssaires ont &eacute;t&eacute; d&eacute;j&agrave; envoy&eacute; aux diff&eacute;rents acteurs ayant interven");
                        }
                        else{

                            alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> Souhaitez-vous envoyer un message de remerciemenu au client ? Cette action confirmera que le colis a &eacute;t&eacute; bel et bien livr&eacute;",function(event){

                                if(event) {

                                    $scope.loadingPage = true;

                                    $scope.objets        = item;

                                    $scope.objets.action = "Remerciement";

                                    $http({

                                        url: $scope.api+"/notification-acteur",

                                        method: "PUT",

                                        data: JSON.stringify($scope.objets),

                                        async: true,

                                        cache: false,

                                        headers: {

                                            'Content-Type': 'application/json',

                                            'Authorization': 'Bearer ' + $scope.jwt_token,

                                            'Accept': 'application/json',
                                        }

                                    }).then(function(data, status, headers, config){

                                        $scope.loadingPage = false;

                                        if (data.data.information.requestCode === 201) {

                                            $scope.findall();

                                        }
                                        else {
                                            alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                        }

                                    },function(data, status, headers, config){

                                        $scope.loadingPage = false;

                                        alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : Impossible de traiter votre requ&ecirc;te. Si le probl&egrave;me persiste nous vous invitons à contacter l'administrateur.");
                                    });
                                }
                            });
                        }
                    }
                }
            }
            else
                $state.go("404");
        });
})();