(function() {

    'use strict';

    DiGital

        .controller('TopBarController', function ($rootScope, $scope, $location, ROOT_URL, $http, $state, alertify,
                                                     $cookies,  $log) {

            if($cookies.getObject('uData')){

                $scope.api          = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;

                $scope.jwt_token    = $cookies.getObject('jw_token');

                $scope.viewModule   = $cookies.getObject('uData').profile.module;

                $scope.profileType  = $cookies.getObject('uData').profile.profileType;

                /*$log.info($scope.profileType)
                $log.info($cookies.getObject('uData').profile.profileType)*/
            }
            else{

                $state.go("login");
            }
        })

        .controller('DashboardController', function ($rootScope, $scope, $location, ROOT_URL, $http, $state, alertify,
                                                     $cookies, DTOptionsBuilder,DTColumnDefBuilder, $log, $modal, $filter) {

            console.clear();

            $log.info(" =========================== [ DashboardController ] =========================== ")

            if($cookies.getObject('uData')){

                $scope.api = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;

                $scope.jwt_token = $cookies.getObject('jw_token');

                $scope.viewModuleDash   = $cookies.getObject('uData').profile.module;

                $scope.profileTypeDash  = $cookies.getObject('uData').profile.profileType;

                //$log.info("Type Profil ===== ===== ========= ===== " + $scope.profileTypeDash)

                //$log.info($cookies.getObject('uData').profile)

                $scope.atDate  = $filter('date')(new Date(), 'dd-MM-yyyy');

                $scope.dtOptions = DTOptionsBuilder
                    .newOptions()
                    .withBootstrap("responsive",!0)
                    .withOption('order', [[0, 'desc']])
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


                //$log.info($cookies.getObject('uData').profile.module)

                //input.substring(0,1).toUpperCase()+input.substring(1).toLowerCase()

                $scope.getStat = function () {

                    $scope.loadingPage = true;

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/get-stat-by-profile/"+$cookies.getObject('uData').profile.idDigital+"/"+$cookies.getObject('uData').profile.module,

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
                $scope.getStat();


                $scope.viewDetailsMessage = function(item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/dashboard/details/assurance-message.html',
                        controller: 'InstanceDetailsMessage',
                        resolve: {
                            item: function () {
                                return item || null;
                            }
                        }
                    });

                    modalInstance.result.then(function (result) {
                        console.log('result >>>> >>>>>> >>>>' + item);
                    }, function () {
                        $scope.getStat();
                    });
                }
            }
            else{

                $state.go("login");
            }
        })

        .controller('NosOffresController', function ($rootScope, $scope, $location, ROOT_URL, $http,
                                                     $state, alertify, $cookies, $log, $modal) {

            if($cookies.getObject('uData')){

                $scope.tab = 1;

                $scope.setTab = function(newTab){
                    $scope.tab = newTab;
                };

                $scope.isSet = function(tabNum){
                    return $scope.tab === tabNum;
                };


                $scope.openFrmRechargeOffreEntreprise = function(event, item) {

                    const modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/offre/frm-recharger-mon-compte-sms-offre-entreprise.html',

                        controller: 'InstanceRechargerCompteSmsOffreEntreprise',

                        resolve: {
                            item: function () {
                                return item || null;
                            }
                        }
                    });

                    modalInstance.result.then(function (result) {
                        console.log('result: ' + item);
                    }, function () {

                        //$scope.findall();
                    });
                }
            }
            else{
                $state.go("login");
            }
        })

        .controller('InstanceRechargerCompteSmsOffreEntreprise', function($scope, $modalInstance, item, $http, alertify, $location,
                                                           ROOT_URL, $state, $cookies, $log) {

            console.clear();

            $scope.jwt_token        = $cookies.getObject('jw_token');

            $scope.api 			    = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;

            $scope.getListFormule = function () {

                $http({

                    dataType: 'jsonp',

                    url: $scope.api+"/find-all-type-offre-sms-entreprise",

                    method: "GET",

                    async: true,

                    cache: false,

                    headers: {

                        'Content-Type': 'application/json',

                        'Authorization': 'Bearer ' + $scope.jwt_token,

                        'Accept': 'application/json',
                    }
                }).then(function(data){

                    $scope.getList = data.data;});
            };


            $scope.getListTypePAid = function () {

                $http({

                    dataType: 'jsonp',

                    url: $scope.api+"/find-all-type-offre-sms-entreprise",

                    method: "GET",

                    async: true,

                    cache: false,

                    headers: {

                        'Content-Type': 'application/json',

                        'Authorization': 'Bearer ' + $scope.jwt_token,

                        'Accept': 'application/json',
                    }
                }).then(function(data){

                    $scope.getList1 = data.data;});
            };


            $scope.listTypePaiement = function () {

                $http({

                    dataType: 'jsonp',

                    url: $scope.api+"/find-all-type-paiement",

                    method: "GET",

                    async: true,

                    cache: false,

                    headers: {

                        'Content-Type': 'application/json',

                        'Authorization': 'Bearer ' + $scope.jwt_token,

                        'Accept': 'application/json',
                    }
                }).then(function(data){

                    $scope.getListTypePaiement = data.data;});
            };

            if($cookies.getObject('uData')){

                $scope.getListFormule();

                $scope.getListTypePAid();

                $scope.listTypePaiement();

                $scope.api                          = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token                    = $cookies.getObject('jw_token');
                $scope.api 			                = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.style 		                = "btn-primary";
                $scope.items 		                = {};
                $scope.nomBtn 		                = "Enregistrer";
                $scope.titleFrm                     = "Demander un rechargement de mon compte SMS - Offre Entreprise";
                $scope.url                          = "create-new-rechargement-sms-account-entreprise";
                $scope.items.profile                = $cookies.getObject('uData').profile;

                $scope.nbreSms                      = 0;
                $scope.totalPayer                   = 0;
                $scope.montantRechargement          = 0;

                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };

                $scope.showdata = function(donnes) {

                    if(donnes !== undefined){

                        $scope.nbreSms              = donnes.nbreSms;
                        $scope.totalPayer           = donnes.totalPayer;
                        $scope.montantRechargement  = donnes.montantRechargement;
                    }
                    else{
                        $scope.nbreSms              = 0;
                        $scope.totalPayer           = 0;
                        $scope.montantRechargement  = 0;
                    }
                };


                if (item !== null && item !=='') {

                    $scope.titleFrm = "Modifier la demande d'un rechargement de mon compte SMS";

                    $scope.url = 'update-rechargement-sms-account';

                    $scope.style = "btn-success";

                    $scope.nomBtn = "Appliquer"

                    $scope.items = item;
                }



                $scope.saveDemandeRechargement = function () {

                    $scope.loadingPage = true;

                    var route = $scope.api + '/' + $scope.url;

                    var method = "POST";

                    if (item !== null && item.id !== undefined) {

                        route = $scope.api + '/' + $scope.url;

                        method = "PUT";
                    }

                    $http({

                        url: route,

                        method: method,

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

                            if (data.data.requestCode === 201) {

                                $modalInstance.dismiss('cancel');

                            } else if (data.data.requestCode === 400) {

                                alertify.alert("<strong>Di-Gital web</strong> : " + data.data.message);
                            }
                            else {
                                alertify.alert("<strong>Di-Gital web</strong> : " + data.data.message);
                            }
                        }, function (data, status, headers, config) {

                            $scope.loadingPage = false;

                            alertify.alert("<strong>Di-Gital web</strong> : " + data.data.message);
                        });
                };

            }
            else{
                $state.go("404");
            }
        })

        .controller('SessionVerouilleController', function ($rootScope, $scope, $location, ROOT_URL, $http, $state,
                                                            $cookies, alertify, $log, $crypto ) {

            if($cookies.getObject('uData')){

                $scope.api = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;

                $scope.jwt_token = $cookies.getObject('jw_token');

                $scope.items = {};


                $scope.toUnlock = function(){

                    if($cookies.getObject('pwd') && $scope.jwt_token){

                        if($crypto.decrypt($cookies.getObject('pwd')) === $scope.items.passwordSession){

                            $state.go("mainpage.dashboard");
                        }
                        else{
                            alertify.alert("<strong>Di-Gital web</strong> : Mot de passe saisi pour l'ouverture de votre session est incorrecte.");
                        }
                    }
                    else{

                        alertify.alert("<strong>Di-Gital web</strong> : Impossible de verouiller votre session.");
                    }
                }
            }
            else{
                $state.go("login");
            }
        })

        .controller('RechargerCompteSmsController', function ($rootScope, $scope, $location, ROOT_URL, $http,
                                                              $state, alertify, $cookies, $log, $modal) {

            console.clear();

            if($cookies.getObject('uData')){

                $scope.loadingPage      = true;

                $scope.jwt_token        = $cookies.getObject('jw_token');

                $scope.api 			    = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;


                $scope.findall = function () {

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-rechargement-compte-sms-by-entrpise/"+$cookies.getObject('uData').profile.idDigital,

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
                        $scope.objets = data.data;
                    });
                };
                $scope.findall();


                $scope.openFrmRecharge = function(event, item) {

                    const modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/offre/frm-recharger-mon-compte-sms.html',

                        controller: 'InstanceRechargerCompteSms',

                        resolve: {
                            item: function () {
                                return item || null;
                            }
                        }
                    });

                    modalInstance.result.then(function (result) {
                        console.log('result: ' + item);
                    }, function () {

                        $scope.findall();
                    });
                }
            }
            else{

                $state.go("login");
            }
        })

        .controller('InstanceRechargerCompteSms', function($scope, $modalInstance, item, $http, alertify, $location,
                                                     ROOT_URL, $state, $cookies, $log) {

            console.clear();

            $scope.jwt_token        = $cookies.getObject('jw_token');

            $scope.api 			    = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;

            $scope.getListTypepaiement = function () {

                $log.info("Charegemnt de la liste des type de paiement")

                $http({

                    dataType: 'jsonp',

                    url: $scope.api+"/find-all-type-paiement",

                    method: "GET",

                    async: true,

                    cache: false,

                    headers: {

                        'Content-Type': 'application/json',

                        'Authorization': 'Bearer ' + $scope.jwt_token,

                        'Accept': 'application/json',
                    }
                }).then(function(data){

                    $scope.getList = data.data;});
            };

            if($cookies.getObject('uData')){

                $scope.getListTypepaiement();

                $scope.api                          = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token                    = $cookies.getObject('jw_token');
                $scope.api 			                = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.style 		                = "btn-primary";
                $scope.items 		                = {};
                $scope.nomBtn 		                = "Enregistrer";
                $scope.titleFrm                     = "Demander un rechargement de mon compte SMS";
                $scope.url                          = "create-new-rechargement-sms-account";
                $scope.items.profile                = $cookies.getObject('uData').profile;

                $scope.items.montantRechargement    = 5000;
                $scope.items.nbreSms                = Math.ceil($scope.items.montantRechargement/25);
                $scope.items.totalPayer             = $scope.items.montantRechargement + parseInt($scope.items.montantRechargement * 0.03);
                $scope.items.typeFormule            = "OFFRE INDIVIDUELLE";
                $scope.items.nomFormule             = "INDIVIDUAL PACK";

                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };


                if (item !== null && item !=='') {

                    $scope.titleFrm = "Modifier la demande d'un rechargement de mon compte SMS";

                    $scope.url = 'update-rechargement-sms-account';

                    $scope.style = "btn-success";

                    $scope.nomBtn = "Appliquer"

                    $scope.items = item;
                }

                $scope.putAmount = function(amount) {

                    if (amount !== undefined){

                        //$log.info(amount);

                        //Nombre de SMS
                        $scope.items.nbreSms    = Math.ceil(amount/25);

                        $scope.items.totalPayer = amount + parseInt(amount * 0.03);
                    }
                };


                $scope.saveRequeste = function () {

                    $scope.loadingPage = true;

                    var route = $scope.api + '/' + $scope.url;

                    var method = "POST";

                    if (item !== null && item.id !== undefined) {

                        route = $scope.api + '/' + $scope.url;

                        method = "PUT";
                    }

                    $http({

                        url: route,

                        method: method,

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

                        if (data.data.requestCode === 201) {

                            $modalInstance.dismiss('cancel');

                        } else if (data.data.requestCode === 400) {

                            alertify.alert("<strong>Di-Gital web</strong> : " + data.data.message);
                        }
                        else {
                            alertify.alert("<strong>Di-Gital web</strong> : " + data.data.message);
                        }
                    }, function (data, status, headers, config) {

                        $scope.loadingPage = false;

                        alertify.alert("<strong>Di-Gital web</strong> : " + data.data.message);
                    });
                };

            }
            else{
                $state.go("404");
            }
        })

        .controller('PaiementClientController', function ($rootScope, $scope, $location, ROOT_URL, $http,
                                                              $state, alertify, $cookies, $log, $modal,
                                                          DTOptionsBuilder,DTColumnDefBuilder) {
           console.clear();

            if($cookies.getObject('uData')){

                $scope.loadingPage = false;

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
                    DTColumnDefBuilder.newColumnDef(7).notSortable()
                ];

                $scope.searchFrm        = false;

                $scope.jwt_token        = $cookies.getObject('jw_token');

                $scope.api 			    = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;

                $scope.request          = {};

                $scope.launch           = 0;


                $scope.getRequestCustomer = function () {

                    $scope.loadingPage = true;

                    if($scope.request.refPaiement === undefined && $scope.request.numPayeur === undefined) {

                        $scope.launch = 0;

                        alertify.alert("<strong>Di-Gital web</strong> : Veuillez renseigner au moins un champs dans la partie recherche avanc&eacute;e.");

                        $scope.loadingPage = false;
                    }
                    else if($scope.request.refPaiement === undefined && $scope.request.numPayeur !== undefined) {

                        $scope.launch = 1;
                    }
                    else if($scope.request.refPaiement !== undefined && $scope.request.numPayeur !== undefined) {

                        $scope.launch = 1;
                    }
                    else if($scope.request.refPaiement !== undefined && $scope.request.numPayeur === undefined) {

                        $scope.launch = 1;
                    }

                    if($scope.launch === 1) {

                        $http({

                            url: $scope.api + '/recherche-paiement-en-attente',

                            method: "POST",

                            data: angular.toJson($scope.request),

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

                                $scope.searchFrm    = true;

                                $scope.result       = data.data;

                                $scope.request      = {};
                            }

                        }, function (data, status, headers, config) {

                            $scope.loadingPage  = false;

                            $scope.searchFrm    = false;

                            alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                        });
                    }
                }


                $scope.findall = function (donnees){

                     if(donnees != null){

                         $scope.loadingPage  = true;

                         $scope.request.numPayeur = donnees.numPayeur;

                         $http({

                             url: $scope.api + '/recherche-paiement-en-attente',

                             method: "POST",

                             data: angular.toJson($scope.request),

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

                             $scope.request      = {};

                             if(data.status === 500){

                                 alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                             }
                             else{

                                 if(data.data.length > 0){

                                     $scope.searchFrm    = true;

                                     $scope.result       = data.data;
                                 }
                                 else{

                                     $scope.searchFrm    = false;
                                 }
                             }

                         }, function (data, status, headers, config) {

                             $scope.loadingPage  = false;

                             $scope.searchFrm    = false;

                             $scope.request      = {};
                         })
                     }
                     else{
                         $scope.request      = {};
                         $scope.searchFrm    = false;
                     }
                }

                $scope.openFrmProcessing = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/offre/frm-processing-request.html',
                        controller: 'InstanceProcessingRequest',
                        resolve: {
                            item: function () {
                                return item;
                            }
                        }
                    });

                    modalInstance.result.then(function (result) {
                        console.log('result: ' + item);
                    }, function () {
                        $scope.findall(item);
                    });
                }
            }
            else{

                $state.go("login");
            }
        })

        .controller('InstanceProcessingRequest', function($scope, $modalInstance, item, $http, alertify, $location,
                                                 ROOT_URL, $state, $cookies, $log) {

            if($cookies.getObject('uData')){
                $scope.api          = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token    = $cookies.getObject('jw_token');

                $scope.items 		= {};

                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };


                if (item !== null && item !=='') {

                    $scope.nomBtn   = "Traiter la demande";

                    $scope.items    = item;
                }


                $scope.processingRequest = function () {

                    $scope.loadingPage = true;

                    $http({

                        url: $scope.api + '/traitement-requete-rechargement-sms-account',

                        method: "PUT",

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

                            if (data.data.requestCode === 200) {

                                $modalInstance.dismiss();
                            }
                            else if (data.data.requestCode === 400) {

                                alertify.alert("<strong>Di-Gital web</strong> : " + data.data.message);
                            }
                            else {

                                alertify.alert("<strong>Di-Gital web</strong> : Impossible de traiter votre requ&ecirc;te.");
                            }

                        }, function (data) {

                            $scope.loadingPage = false;
                            alertify.alert("<strong>Di-Gital web</strong> : " + data.data.message);
                        });
                }
            }
            else{
                $state.go("404");
            }
        })


        .controller('InstanceDetailsMessage', function($scope, $modalInstance, item, $http, alertify, $location,
                                                        ROOT_URL, $state, $cookies) {
            if($cookies.getObject('uData')){
                $scope.items 		= {};


                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };


                if (item !== null && item !=='') {

                    $scope.items = item;
                }


            }
            else{
                $state.go("404");
            }
        })
})();