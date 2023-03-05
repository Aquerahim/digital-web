(function() {

    'use strict';

    DiGital

        .controller('BanqueDepotBoissonController', function($rootScope, $scope, $location, $http, alertify, $state,
                                                                $timeout, $cookies, ROOT_URL, $log, $modal, DTOptionsBuilder,
                                                                DTColumnDefBuilder) {
            if($cookies.getObject('uData')) {

                $scope.api = $location.protocol() + "://" + $location.host() + ':' + $location.port() + "" + ROOT_URL.api;

                $scope.jwt_token = $cookies.getObject('jw_token');


                $scope.loadingPage = false;

                $scope.datas = {};

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
                    DTColumnDefBuilder.newColumnDef(4).notSortable()
                ];



                $scope.findAllData = function () {

                    $scope.loadingPage = true;

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-banque",

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
                                //$scope.existData = data.data.length > 0;
                            },
                            function(data, status, xhrStatus){
                                $scope.loadingPage = false;
                            });
                };
                $scope.findAllData();


                $scope.openFrmBanque = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/e-module/depot/parametre/banque/banque-form.html',
                        
                        controller: 'InstanceParametreBanque',
                        
                        resolve: {
                            item: function () {
                                return item || null;
                            }
                        }
                    });

                    modalInstance.result.then(function (result) {
                        //console.log('result: ' + item);
                    }, function () {
                        $scope.findAllData();
                    });
                }


                $scope.actionOneItemBanque = function (id, item, action) {

                    if(action === 1){

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir supprimer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/delete-item-banque",

                                    method: "PUT",
                                    data: JSON.stringify(item),

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

                                        $scope.findAllData();

                                    }
                                    else {
                                        alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                    }

                                },function(data, status, headers, config){
                                    $scope.loadingPage = false;
                                    alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.message);
                                });
                            }
                        });
                    }
                    else{

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir activer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/active-item-banque",

                                    method: "PUT",
                                    data: JSON.stringify(item),

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

                                        $scope.findAllData();

                                    }
                                    else {
                                        alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                    }

                                },function(data, status, headers, config){
                                    $scope.loadingPage = false;
                                    alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                });
                            }
                        });
                    }

                };
            }
            else{
                $state.go("404");
            }
        })


        .controller('InstanceParametreBanque', function($scope, $modalInstance, item, $http, alertify, $location,
                                                              ROOT_URL, $state, $cookies, $log) {

            if($cookies.getObject('uData')){

                $scope.api                  = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token            = $cookies.getObject('jw_token');

                $scope.api 			        = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.style 		        = "btn-primary";
                $scope.items 		        = {};
                $scope.nomBtn 		        = "Enregistrer";
                $scope.titleFrm             = "Ajouter une nouvelle banque";
                $scope.url                  = "create-new-banque";

                $scope.items.entreprises    = $cookies.getObject('uData').profile.gareRoutiere.compagnie;
                $scope.items.profile        = $cookies.getObject('uData').profile;


                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };


                if (item !== null && item !=='') {

                    $scope.titleFrm = "Modifier informations sur la banque";

                    $scope.url = 'update-banque';

                    $scope.style = "btn-success";

                    $scope.nomBtn = "Appliquer"

                    $scope.items = item;
                }


                $scope.saveBanque = function () {

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

                            if (data.data.information.requestCode === 201) {

                                $modalInstance.dismiss()
                            } else if (data.data.information.requestCode === 400) {
                                alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                            } else {
                                alertify.alert("<strong>Di-Gital web</strong> : Impossible de traiter votre requ&ecirc;te.");
                            }
                        }, function (data, status, headers, config) {
                            $scope.loadingPage = false;

                            alertify.alert("<strong>Di-Gital web</strong> : " + ata.message);
                        });
                };
            }
            else{
                $state.go("404");
            }
        })


        .controller('CompteBancaireController', function($rootScope, $scope, $location, $http, alertify, $state,
                                                             $timeout, $cookies, ROOT_URL, $log, $modal, DTOptionsBuilder,
                                                             DTColumnDefBuilder) {
            if($cookies.getObject('uData')) {

                $scope.api = $location.protocol() + "://" + $location.host() + ':' + $location.port() + "" + ROOT_URL.api;

                $scope.jwt_token = $cookies.getObject('jw_token');


                $scope.loadingPage = false;

                $scope.datas = {};

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
                    DTColumnDefBuilder.newColumnDef(4).notSortable()
                ];



                $scope.findAllData = function () {

                    $scope.loadingPage = true;

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-compte-bancaire",

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
                                //$scope.existData = data.data.length > 0;
                            },
                            function(data, status, xhrStatus){
                                $scope.loadingPage = false;
                            });
                };
                $scope.findAllData();


                $scope.openFrmCompteBancaire = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/e-module/depot/parametre/compte-bancaire/compte-bancaire-frm.html',

                        controller: 'InstanceParametreCompteBancaire',

                        resolve: {
                            item: function () {
                                return item || null;
                            }
                        }
                    });

                    modalInstance.result.then(function (result) {
                        //console.log('result: ' + item);
                    }, function () {
                        $scope.findAllData();
                    });
                }


                $scope.actionOneItemCompteBancaire = function (id, item, action) {

                    if(action === 1){

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir supprimer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/delete-item-banque",

                                    method: "PUT",
                                    data: JSON.stringify(item),

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

                                        $scope.findAllData();

                                    }
                                    else {
                                        alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                    }

                                },function(data, status, headers, config){
                                    $scope.loadingPage = false;
                                    alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.message);
                                });
                            }
                        });
                    }
                    else{

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir activer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/active-item-banque",

                                    method: "PUT",
                                    data: JSON.stringify(item),

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

                                        $scope.findAllData();

                                    }
                                    else {
                                        alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                    }

                                },function(data, status, headers, config){
                                    $scope.loadingPage = false;
                                    alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                });
                            }
                        });
                    }

                };
            }
            else{
                $state.go("404");
            }
        })


        .controller('InstanceParametreCompteBancaire', function($scope, $modalInstance, item, $http, alertify, $location,
                                                        ROOT_URL, $state, $cookies, $log) {

            if($cookies.getObject('uData')){

                $scope.api                  = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token            = $cookies.getObject('jw_token');

                $scope.api 			        = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.style 		        = "btn-primary";
                $scope.items 		        = {};
                $scope.nomBtn 		        = "Enregistrer";
                $scope.titleFrm             = "Ajouter un nouveau compte bancaire";
                $scope.url                  = "create-new-compte-bancaire";

                $scope.items.entreprises    = $cookies.getObject('uData').profile.gareRoutiere.compagnie;
                $scope.items.profile        = $cookies.getObject('uData').profile;


                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };


                if (item !== null && item !=='') {

                    $scope.titleFrm = "Modifier informations sur un compte bancaire";

                    $scope.url = 'update-compte-bancaire';

                    $scope.style = "btn-success";

                    $scope.nomBtn = "Appliquer"

                    $scope.items = item;
                }


                $scope.findAllBanqueListe = function () {

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api + '/liste-banque',

                        method: "GET",

                        async: true,

                        cache: false,

                        headers: {

                            'Content-Type': 'application/json',

                            'Authorization': 'Bearer ' + $scope.jwt_token,

                            'Accept': 'application/json',
                        }

                    }).then(function (data) {
                        $scope.findAllBanqueListe = data.data;
                    });
                }();


                $scope.saveCompteBancaire = function () {

                    $scope.items.numeroDeCompte = $scope.items.numeroDeCompte.substring(0, 5)+"-"+$scope.items.numeroDeCompte.substring(5, $scope.items.numeroDeCompte.length-2)+"-"+$scope.items.numeroDeCompte.substring(17, $scope.items.numeroDeCompte.length);

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

                        if (data.data.information.requestCode === 201) {

                            $modalInstance.dismiss()
                        } else if (data.data.information.requestCode === 400) {
                            alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                        } else {
                            alertify.alert("<strong>Di-Gital web</strong> : Impossible de traiter votre requ&ecirc;te.");
                        }
                    }, function (data, status, headers, config) {
                        $scope.loadingPage = false;

                        alertify.alert("<strong>Di-Gital web</strong> : " + ata.message);
                    });
                };
            }
            else{
                $state.go("404");
            }
        })

        .controller('ModeVenteController', function($rootScope, $scope, $location, $http, alertify, $state,
                                                    $timeout, $cookies, ROOT_URL, $log, $modal, DTOptionsBuilder,
                                                    DTColumnDefBuilder) {
            if($cookies.getObject('uData')) {

                $scope.api = $location.protocol() + "://" + $location.host() + ':' + $location.port() + "" + ROOT_URL.api;

                $scope.jwt_token = $cookies.getObject('jw_token');


                $scope.loadingPage = false;

                $scope.datas = {};

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
                    DTColumnDefBuilder.newColumnDef(4).notSortable()
                ];



                $scope.findAllData = function () {

                    $scope.loadingPage = true;

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-mode-de-vente",

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
                                //$scope.existData = data.data.length > 0;
                            },
                            function(data, status, xhrStatus){
                                $scope.loadingPage = false;
                            });
                };
                $scope.findAllData();


                $scope.openFrmModeVente = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/e-module/depot/parametre/mode-vente/mode-de-vente-frm.html',

                        controller: 'InstanceParametreModeVente',

                        resolve: {
                            item: function () {
                                return item || null;
                            }
                        }
                    });

                    modalInstance.result.then(function (result) {
                        //console.log('result: ' + item);
                    }, function () {
                        $scope.findAllData();
                    });
                }


                $scope.actionOneItemModeVente = function (id, item, action) {

                    if(action === 1){

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir supprimer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/delete-item-mode-de-vente",

                                    method: "PUT",
                                    data: JSON.stringify(item),

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

                                        $scope.findAllData();

                                    }
                                    else {
                                        alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                    }

                                },function(data, status, headers, config){
                                    $scope.loadingPage = false;
                                    alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.message);
                                });
                            }
                        });
                    }
                    else{

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir activer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/active-item-mode-de-vente",

                                    method: "PUT",
                                    data: JSON.stringify(item),

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

                                        $scope.findAllData();

                                    }
                                    else {
                                        alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                    }

                                },function(data, status, headers, config){
                                    $scope.loadingPage = false;
                                    alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                });
                            }
                        });
                    }

                };
            }
            else{
                $state.go("404");
            }
        })


        .controller('InstanceParametreModeVente', function($scope, $modalInstance, item, $http, alertify, $location,
                                                           ROOT_URL, $state, $cookies, $log) {

            if($cookies.getObject('uData')){

                $scope.api                  = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token            = $cookies.getObject('jw_token');

                $scope.api 			        = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.style 		        = "btn-primary";
                $scope.items 		        = {};
                $scope.nomBtn 		        = "Enregistrer";
                $scope.titleFrm             = "Ajouter un nouveau mode de vente";
                $scope.url                  = "create-new-mode-de-vente";


                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };


                if (item !== null && item !=='') {

                    $scope.titleFrm = "Modifier informations sur le mode de vente";

                    $scope.url = 'update-mode-de-vente';

                    $scope.style = "btn-success";

                    $scope.nomBtn = "Appliquer"

                    $scope.items = item;
                }


                $scope.saveModeVente = function () {

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

                            if (data.data.information.requestCode === 201) {

                                $modalInstance.dismiss()
                            } else if (data.data.information.requestCode === 400) {
                                alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                            } else {
                                alertify.alert("<strong>Di-Gital web</strong> : Impossible de traiter votre requ&ecirc;te.");
                            }
                        }, function (data, status, headers, config) {
                            $scope.loadingPage = false;

                            alertify.alert("<strong>Di-Gital web</strong> : " + ata.message);
                        });
                };
            }
            else{
                $state.go("404");
            }
        })


        .controller('NatureDepotController', function($rootScope, $scope, $location, $http, alertify, $state,
                                                      $timeout, $cookies, ROOT_URL, $log, $modal, DTOptionsBuilder,
                                                      DTColumnDefBuilder) {
            if($cookies.getObject('uData')) {

                $scope.api = $location.protocol() + "://" + $location.host() + ':' + $location.port() + "" + ROOT_URL.api;

                $scope.jwt_token = $cookies.getObject('jw_token');


                $scope.loadingPage = false;

                $scope.datas = {};

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
                    DTColumnDefBuilder.newColumnDef(4).notSortable()
                ];



                $scope.findAllData = function () {

                    $scope.loadingPage = true;

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-nature-depot",

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
                                //$scope.existData = data.data.length > 0;
                            },
                            function(data, status, xhrStatus){
                                $scope.loadingPage = false;
                            });
                };
                $scope.findAllData();


                $scope.openFrmNatureDepot = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/e-module/depot/parametre/nature-depot/nature-depot-frm.html',

                        controller: 'InstanceParametreNatureDepot',

                        resolve: {
                            item: function () {
                                return item || null;
                            }
                        }
                    });

                    modalInstance.result.then(function (result) {
                        //console.log('result: ' + item);
                    }, function () {
                        $scope.findAllData();
                    });
                }


                $scope.actionOneItemNatureDepot = function (id, item, action) {

                    if(action === 1){

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir supprimer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/delete-item-nature-depot",

                                    method: "PUT",
                                    data: JSON.stringify(item),

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

                                        $scope.findAllData();

                                    }
                                    else {
                                        alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                    }

                                },function(data, status, headers, config){
                                    $scope.loadingPage = false;
                                    alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.message);
                                });
                            }
                        });
                    }
                    else{

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir activer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/active-item-nature-depot",

                                    method: "PUT",
                                    data: JSON.stringify(item),

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

                                        $scope.findAllData();

                                    }
                                    else {
                                        alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                    }

                                },function(data, status, headers, config){
                                    $scope.loadingPage = false;
                                    alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                });
                            }
                        });
                    }

                };
            }
            else{
                $state.go("404");
            }
        })


        .controller('InstanceParametreNatureDepot', function($scope, $modalInstance, item, $http, alertify, $location,
                                                             ROOT_URL, $state, $cookies, $log) {

            if($cookies.getObject('uData')){

                $scope.api                  = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token            = $cookies.getObject('jw_token');

                $scope.api 			        = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.style 		        = "btn-primary";
                $scope.items 		        = {};
                $scope.nomBtn 		        = "Enregistrer";
                $scope.titleFrm             = "Ajouter un nouveau mode de vente";
                $scope.url                  = "create-new-nature-depot";


                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };


                if (item !== null && item !=='') {

                    $scope.titleFrm = "Modifier informations sur le mode de vente";

                    $scope.url = 'update-nature-depot';

                    $scope.style = "btn-success";

                    $scope.nomBtn = "Appliquer"

                    $scope.items = item;
                }


                $scope.saveNatureDepot = function () {

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

                            if (data.data.information.requestCode === 201) {

                                $modalInstance.dismiss()
                            } else if (data.data.information.requestCode === 400) {
                                alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                            } else {
                                alertify.alert("<strong>Di-Gital web</strong> : Impossible de traiter votre requ&ecirc;te.");
                            }
                        }, function (data, status, headers, config) {
                            $scope.loadingPage = false;

                            alertify.alert("<strong>Di-Gital web</strong> : " + ata.message);
                        });
                };
            }
            else{
                $state.go("404");
            }
        })


        .controller('TypesPaiementController', function($rootScope, $scope, $location, $http, alertify, $state,
                                                       $timeout, $cookies, ROOT_URL, $log, $modal, DTOptionsBuilder,
                                                       DTColumnDefBuilder) {
            if($cookies.getObject('uData')) {

                $scope.api = $location.protocol() + "://" + $location.host() + ':' + $location.port() + "" + ROOT_URL.api;

                $scope.jwt_token = $cookies.getObject('jw_token');


                $scope.loadingPage = false;

                $scope.datas = {};

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
                    DTColumnDefBuilder.newColumnDef(4).notSortable()
                ];



                $scope.findAllData = function () {

                    $scope.loadingPage = true;

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-type-paiement-depot",

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
                                //$scope.existData = data.data.length > 0;
                            },
                            function(data, status, xhrStatus){
                                $scope.loadingPage = false;
                            });
                };
                $scope.findAllData();


                $scope.openFrmTypePaiement = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/e-module/depot/parametre/type-paiement/type-paiement-frm.html',

                        controller: 'InstanceParametreTypePaiement',

                        resolve: {
                            item: function () {
                                return item || null;
                            }
                        }
                    });

                    modalInstance.result.then(function (result) {
                        //console.log('result: ' + item);
                    }, function () {
                        $scope.findAllData();
                    });
                }


                $scope.actionOneItemTypePaiement = function (id, item, action) {

                    if(action === 1){

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir supprimer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/delete-item-type-paiement-depot",

                                    method: "PUT",
                                    data: JSON.stringify(item),

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

                                        $scope.findAllData();

                                    }
                                    else {
                                        alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                    }

                                },function(data, status, headers, config){
                                    $scope.loadingPage = false;
                                    alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.message);
                                });
                            }
                        });
                    }
                    else{

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir activer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/active-item-type-paiement-depot",

                                    method: "PUT",
                                    data: JSON.stringify(item),

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

                                        $scope.findAllData();

                                    }
                                    else {
                                        alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                    }

                                },function(data, status, headers, config){
                                    $scope.loadingPage = false;
                                    alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                });
                            }
                        });
                    }

                };
            }
            else{
                $state.go("404");
            }
        })


        .controller('InstanceParametreTypePaiement', function($scope, $modalInstance, item, $http, alertify, $location,
                                                              ROOT_URL, $state, $cookies, $log) {

            if($cookies.getObject('uData')){

                $scope.api                  = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token            = $cookies.getObject('jw_token');

                $scope.api 			        = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.style 		        = "btn-primary";
                $scope.items 		        = {};
                $scope.nomBtn 		        = "Enregistrer";
                $scope.titleFrm             = "Ajouter un nouveau type de paiement";
                $scope.url                  = "create-new-type-paiement-depot";


                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };


                if (item !== null && item !=='') {

                    $scope.titleFrm = "Modifier informations sur le type de paiement";

                    $scope.url = 'update-type-paiement-depot';

                    $scope.style = "btn-success";

                    $scope.nomBtn = "Appliquer"

                    $scope.items = item;
                }


                $scope.saveTypePaiement = function () {

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

                            if (data.data.information.requestCode === 201) {

                                $modalInstance.dismiss()
                            } else if (data.data.information.requestCode === 400) {
                                alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                            } else {
                                alertify.alert("<strong>Di-Gital web</strong> : Impossible de traiter votre requ&ecirc;te.");
                            }
                        }, function (data, status, headers, config) {
                            $scope.loadingPage = false;

                            alertify.alert("<strong>Di-Gital web</strong> : " + ata.message);
                        });
                };
            }
            else{
                $state.go("404");
            }
        })


        .controller('CategorieBoissonController', function($rootScope, $scope, $location, $http, alertify, $state,
                                                           $timeout, $cookies, ROOT_URL, $log, $modal, DTOptionsBuilder,
                                                           DTColumnDefBuilder) {
            if($cookies.getObject('uData')) {

                $scope.api = $location.protocol() + "://" + $location.host() + ':' + $location.port() + "" + ROOT_URL.api;

                $scope.jwt_token = $cookies.getObject('jw_token');


                $scope.loadingPage = false;

                $scope.datas = {};

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
                    DTColumnDefBuilder.newColumnDef(4).notSortable()
                ];



                $scope.findAllData = function () {

                    $scope.loadingPage = true;

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-categorie-boisson",

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
                                //$scope.existData = data.data.length > 0;
                            },
                            function(data, status, xhrStatus){
                                $scope.loadingPage = false;
                            });
                };
                $scope.findAllData();


                $scope.openFrmCategorieBoisson = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/e-module/depot/parametre/categorie-boisson/categorie-boisson-frm.html',

                        controller: 'InstanceParametreCategorieBoisson',

                        resolve: {
                            item: function () {
                                return item || null;
                            }
                        }
                    });

                    modalInstance.result.then(function (result) {
                        //console.log('result: ' + item);
                    }, function () {
                        $scope.findAllData();
                    });
                }


                $scope.actionOneItemCategorieBoisson = function (id, item, action) {

                    if(action === 1){

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir supprimer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/delete-item-categorie-boisson",

                                    method: "PUT",
                                    data: JSON.stringify(item),

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

                                        $scope.findAllData();

                                    }
                                    else {
                                        alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                    }

                                },function(data, status, headers, config){
                                    $scope.loadingPage = false;
                                    alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.message);
                                });
                            }
                        });
                    }
                    else{

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir activer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/active-item-categorie-boisson",

                                    method: "PUT",
                                    data: JSON.stringify(item),

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

                                        $scope.findAllData();

                                    }
                                    else {
                                        alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                    }

                                },function(data, status, headers, config){
                                    $scope.loadingPage = false;
                                    alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                });
                            }
                        });
                    }

                };
            }
            else{
                $state.go("404");
            }
        })


        .controller('InstanceParametreCategorieBoisson', function($scope, $modalInstance, item, $http, alertify, $location,
                                                                  ROOT_URL, $state, $cookies, $log) {

            if($cookies.getObject('uData')){

                $scope.api                  = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token            = $cookies.getObject('jw_token');

                $scope.api 			        = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.style 		        = "btn-primary";
                $scope.items 		        = {};
                $scope.nomBtn 		        = "Enregistrer";
                $scope.titleFrm             = "Ajouter une nouvelle categorie boisson";
                $scope.url                  = "create-new-categorie-boisson";


                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };


                if (item !== null && item !=='') {

                    $scope.titleFrm = "Modifier informations sur la categorie boisson n° "+item.ordre;

                    $scope.url = 'update-categorie-boisson';

                    $scope.style = "btn-success";

                    $scope.nomBtn = "Appliquer"

                    $scope.items = item;
                }


                $scope.saveCategorieBoisson = function () {

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

                            if (data.data.information.requestCode === 201) {

                                $modalInstance.dismiss()
                            } else if (data.data.information.requestCode === 400) {
                                alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                            } else {
                                alertify.alert("<strong>Di-Gital web</strong> : Impossible de traiter votre requ&ecirc;te.");
                            }
                        }, function (data, status, headers, config) {
                            $scope.loadingPage = false;

                            alertify.alert("<strong>Di-Gital web</strong> : " + ata.message);
                        });
                };
            }
            else{
                $state.go("404");
            }
        })

        .controller('MotifAvanceController', function($rootScope, $scope, $location, $http, alertify, $state,
                                                      $timeout, $cookies, ROOT_URL, $log, $modal, DTOptionsBuilder,
                                                      DTColumnDefBuilder) {
            if($cookies.getObject('uData')) {

                $scope.api = $location.protocol() + "://" + $location.host() + ':' + $location.port() + "" + ROOT_URL.api;

                $scope.jwt_token = $cookies.getObject('jw_token');


                $scope.loadingPage = false;

                $scope.datas = {};

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
                    DTColumnDefBuilder.newColumnDef(4).notSortable()
                ];



                $scope.findAllData = function () {

                    $scope.loadingPage = true;

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-motif-avance-sur-salaire",

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
                                //$scope.existData = data.data.length > 0;
                            },
                            function(data, status, xhrStatus){
                                $scope.loadingPage = false;
                            });
                };
                $scope.findAllData();


                $scope.openFrmMotifAvance = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/e-module/depot/parametre/motif-avance/motif-avance-frm.html',

                        controller: 'InstanceParametreMotifAvance',

                        resolve: {
                            item: function () {
                                return item || null;
                            }
                        }
                    });

                    modalInstance.result.then(function (result) {
                        //console.log('result: ' + item);
                    }, function () {
                        $scope.findAllData();
                    });
                }


                $scope.actionOneItemMotifAvance = function (id, item, action) {

                    if(action === 1){

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir supprimer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/delete-item-motif-avance-sur-salaire",

                                    method: "PUT",
                                    data: JSON.stringify(item),

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

                                        $scope.findAllData();

                                    }
                                    else {
                                        alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                    }

                                },function(data, status, headers, config){
                                    $scope.loadingPage = false;
                                    alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.message);
                                });
                            }
                        });
                    }
                    else{

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir activer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/active-item-motif-avance-sur-salaire",

                                    method: "PUT",
                                    data: JSON.stringify(item),

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

                                        $scope.findAllData();

                                    }
                                    else {
                                        alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                    }

                                },function(data, status, headers, config){
                                    $scope.loadingPage = false;
                                    alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                });
                            }
                        });
                    }

                };
            }
            else{
                $state.go("404");
            }
        })


        .controller('InstanceParametreMotifAvance', function($scope, $modalInstance, item, $http, alertify, $location,
                                                             ROOT_URL, $state, $cookies, $log) {

            if($cookies.getObject('uData')){

                $scope.api                  = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token            = $cookies.getObject('jw_token');

                $scope.api 			        = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.style 		        = "btn-primary";
                $scope.items 		        = {};
                $scope.nomBtn 		        = "Enregistrer";
                $scope.titleFrm             = "Ajouter un nouveau motif";
                $scope.url                  = "create-new-motif-avance-sur-salaire";


                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };


                if (item !== null && item !=='') {

                    $scope.titleFrm = "Modifier informations sur la categorie boisson n° "+item.ordre;

                    $scope.url = 'update-motif-avance-sur-salaire';

                    $scope.style = "btn-success";

                    $scope.nomBtn = "Appliquer"

                    $scope.items = item;
                }


                $scope.saveMotifAvance = function () {

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

                            if (data.data.information.requestCode === 201) {

                                $modalInstance.dismiss()
                            } else if (data.data.information.requestCode === 400) {
                                alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                            } else {
                                alertify.alert("<strong>Di-Gital web</strong> : Impossible de traiter votre requ&ecirc;te.");
                            }
                        }, function (data, status, headers, config) {
                            $scope.loadingPage = false;

                            alertify.alert("<strong>Di-Gital web</strong> : " + ata.message);
                        });
                };
            }
            else{
                $state.go("404");
            }
        })


        .controller('DiplomeController', function($rootScope, $scope, $location, $http, alertify, $state,
                                                  $timeout, $cookies, ROOT_URL, $log, $modal, DTOptionsBuilder,
                                                  DTColumnDefBuilder) {
            if($cookies.getObject('uData')) {

                $scope.api = $location.protocol() + "://" + $location.host() + ':' + $location.port() + "" + ROOT_URL.api;

                $scope.jwt_token = $cookies.getObject('jw_token');


                $scope.loadingPage = false;

                $scope.datas = {};

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
                    DTColumnDefBuilder.newColumnDef(4).notSortable()
                ];



                $scope.findAllData = function () {

                    $scope.loadingPage = true;

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-diplome",

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
                                //$scope.existData = data.data.length > 0;
                            },
                            function(data, status, xhrStatus){
                                $scope.loadingPage = false;
                            });
                };
                $scope.findAllData();


                $scope.openFrmDiplome = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/e-module/depot/parametre/diplome/diplome-frm.html',

                        controller: 'InstanceParametreDiplome',

                        resolve: {
                            item: function () {
                                return item || null;
                            }
                        }
                    });

                    modalInstance.result.then(function (result) {
                        //console.log('result: ' + item);
                    }, function () {
                        $scope.findAllData();
                    });
                }


                $scope.actionOneItemDiplome = function (id, item, action) {

                    if(action === 1){

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir supprimer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/delete-item-diplome",

                                    method: "PUT",
                                    data: JSON.stringify(item),

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

                                        $scope.findAllData();

                                    }
                                    else {
                                        alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                    }

                                },function(data, status, headers, config){
                                    $scope.loadingPage = false;
                                    alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.message);
                                });
                            }
                        });
                    }
                    else{

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir activer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/active-item-diplome",

                                    method: "PUT",
                                    data: JSON.stringify(item),

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

                                        $scope.findAllData();

                                    }
                                    else {
                                        alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                    }

                                },function(data, status, headers, config){
                                    $scope.loadingPage = false;
                                    alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                });
                            }
                        });
                    }

                };
            }
            else{
                $state.go("404");
            }
        })


        .controller('InstanceParametreDiplome', function($scope, $modalInstance, item, $http, alertify, $location,
                                                         ROOT_URL, $state, $cookies, $log) {

            if($cookies.getObject('uData')){

                $scope.api                  = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token            = $cookies.getObject('jw_token');

                $scope.api 			        = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.style 		        = "btn-primary";
                $scope.items 		        = {};
                $scope.nomBtn 		        = "Enregistrer";
                $scope.titleFrm             = "Ajouter un nouveau diplome";
                $scope.url                  = "create-new-diplome";


                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };


                if (item !== null && item !=='') {

                    $scope.titleFrm = "Modifier informations sur la categorie boisson n° "+item.ordre;

                    $scope.url = 'update-diplome';

                    $scope.style = "btn-success";

                    $scope.nomBtn = "Appliquer"

                    $scope.items = item;
                }


                $scope.saveDiplome = function () {

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

                            if (data.data.information.requestCode === 201) {

                                $modalInstance.dismiss()
                            } else if (data.data.information.requestCode === 400) {
                                alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                            } else {
                                alertify.alert("<strong>Di-Gital web</strong> : Impossible de traiter votre requ&ecirc;te.");
                            }
                        }, function (data, status, headers, config) {
                            $scope.loadingPage = false;

                            alertify.alert("<strong>Di-Gital web</strong> : " + ata.message);
                        });
                };
            }
            else{
                $state.go("404");
            }
        })


        .controller('PosteOccupeController', function($rootScope, $scope, $location, $http, alertify, $state,
                                                      $timeout, $cookies, ROOT_URL, $log, $modal, DTOptionsBuilder,
                                                      DTColumnDefBuilder) {
            if($cookies.getObject('uData')) {

                $scope.api = $location.protocol() + "://" + $location.host() + ':' + $location.port() + "" + ROOT_URL.api;

                $scope.jwt_token = $cookies.getObject('jw_token');


                $scope.loadingPage = false;

                $scope.datas = {};

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
                    DTColumnDefBuilder.newColumnDef(4).notSortable()
                ];



                $scope.findAllData = function () {

                    $scope.loadingPage = true;

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-poste-occupe",

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
                                //$scope.existData = data.data.length > 0;
                            },
                            function(data, status, xhrStatus){
                                $scope.loadingPage = false;
                            });
                };
                $scope.findAllData();


                $scope.openFrmPosteOccupe = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/e-module/depot/parametre/poste-occupe/poste-occupe-frm.html',

                        controller: 'InstanceParametrePosteOccupe',

                        resolve: {
                            item: function () {
                                return item || null;
                            }
                        }
                    });

                    modalInstance.result.then(function (result) {
                        //console.log('result: ' + item);
                    }, function () {
                        $scope.findAllData();
                    });
                }


                $scope.actionOneItemPosteOccupe = function (id, item, action) {

                    if(action === 1){

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir supprimer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/delete-item-poste-occupe",

                                    method: "PUT",
                                    data: JSON.stringify(item),

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

                                        $scope.findAllData();

                                    }
                                    else {
                                        alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                    }

                                },function(data, status, headers, config){
                                    $scope.loadingPage = false;
                                    alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.message);
                                });
                            }
                        });
                    }
                    else{

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir activer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/active-item-poste-occupe",

                                    method: "PUT",
                                    data: JSON.stringify(item),

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

                                        $scope.findAllData();

                                    }
                                    else {
                                        alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                    }

                                },function(data, status, headers, config){
                                    $scope.loadingPage = false;
                                    alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                });
                            }
                        });
                    }

                };
            }
            else{
                $state.go("404");
            }
        })


        .controller('InstanceParametrePosteOccupe', function($scope, $modalInstance, item, $http, alertify, $location,
                                                             ROOT_URL, $state, $cookies, $log) {

            if($cookies.getObject('uData')){

                $scope.api                  = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token            = $cookies.getObject('jw_token');

                $scope.api 			        = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.style 		        = "btn-primary";
                $scope.items 		        = {};
                $scope.nomBtn 		        = "Enregistrer";
                $scope.titleFrm             = "Ajouter un nouveau poste occupé";
                $scope.url                  = "create-new-poste-occupe";


                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };


                if (item !== null && item !=='') {

                    $scope.titleFrm = "Modifier informations sur la categorie boisson n° "+item.ordre;

                    $scope.url = 'update-poste-occupe';

                    $scope.style = "btn-success";

                    $scope.nomBtn = "Appliquer"

                    $scope.items = item;
                }


                $scope.savePosteOccupe = function () {

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

                            if (data.data.information.requestCode === 201) {

                                $modalInstance.dismiss()
                            } else if (data.data.information.requestCode === 400) {
                                alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                            } else {
                                alertify.alert("<strong>Di-Gital web</strong> : Impossible de traiter votre requ&ecirc;te.");
                            }
                        }, function (data, status, headers, config) {
                            $scope.loadingPage = false;

                            alertify.alert("<strong>Di-Gital web</strong> : " + ata.message);
                        });
                };
            }
            else{
                $state.go("404");
            }
        })


        //CONTROLLER SERVICES
        .controller('FournisseurController', function($rootScope, $scope, $location, $http, alertify, $state,
                                                      $timeout, $cookies, ROOT_URL, $log, $modal, DTOptionsBuilder,
                                                      DTColumnDefBuilder) {
            if($cookies.getObject('uData')) {

                $scope.api = $location.protocol() + "://" + $location.host() + ':' + $location.port() + "" + ROOT_URL.api;

                $scope.jwt_token = $cookies.getObject('jw_token');


                $scope.loadingPage = false;

                $scope.datas = {};

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
                    DTColumnDefBuilder.newColumnDef(5).notSortable()
                ];



                $scope.findAllData = function () {

                    $scope.loadingPage = true;

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-forunisseur",

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
                                //$scope.existData = data.data.length > 0;
                            },
                            function(data, status, xhrStatus){
                                $scope.loadingPage = false;
                            });
                };
                $scope.findAllData();


                $scope.openFrmFournisseur = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/e-module/depot/service/fournisseur/fournisseur-frm.html',

                        controller: 'InstanceParametreFournisseur',

                        resolve: {
                            item: function () {
                                return item || null;
                            }
                        }
                    });

                    modalInstance.result.then(function (result) {
                        //console.log('result: ' + item);
                    }, function () {
                        $scope.findAllData();
                    });
                }


                $scope.actionOneItemFournisseur = function (id, item, action) {

                    if(action === 1){

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir supprimer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/delete-item-forunisseur",

                                    method: "PUT",
                                    data: JSON.stringify(item),

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

                                        $scope.findAllData();

                                    }
                                    else {
                                        alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                    }

                                },function(data, status, headers, config){
                                    $scope.loadingPage = false;
                                    alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.message);
                                });
                            }
                        });
                    }
                    else{

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir activer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/active-item-forunisseur",

                                    method: "PUT",
                                    data: JSON.stringify(item),

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

                                        $scope.findAllData();

                                    }
                                    else {
                                        alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                    }

                                },function(data, status, headers, config){
                                    $scope.loadingPage = false;
                                    alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                });
                            }
                        });
                    }

                };
            }
            else{
                $state.go("404");
            }
        })


        .controller('InstanceParametreFournisseur', function($scope, $modalInstance, item, $http, alertify, $location,
                                                             ROOT_URL, $state, $cookies, $log) {

            if($cookies.getObject('uData')){

                $scope.api                  = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token            = $cookies.getObject('jw_token');

                $scope.api 			        = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.style 		        = "btn-primary";
                $scope.items 		        = {};
                $scope.nomBtn 		        = "Enregistrer";
                $scope.titleFrm             = "Ajouter un nouveau fournisseur";
                $scope.url                  = "create-new-forunisseur";

                $scope.items.entreprises    = $cookies.getObject('uData').profile.gareRoutiere.compagnie;
                $scope.items.profile        = $cookies.getObject('uData').profile;


                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };


                if (item !== null && item !=='') {

                    $scope.titleFrm = "Modifier informations sur le fournisseur n° "+item.ordre;

                    $scope.url = 'update-forunisseur';

                    $scope.style = "btn-success";

                    $scope.nomBtn = "Appliquer"

                    $scope.items = item;
                }


                $scope.saveFournisseur = function () {

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

                            if (data.data.information.requestCode === 201) {

                                $modalInstance.dismiss()
                            } else if (data.data.information.requestCode === 400) {
                                alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                            } else {
                                alertify.alert("<strong>Di-Gital web</strong> : Impossible de traiter votre requ&ecirc;te.");
                            }
                        }, function (data, status, headers, config) {
                            $scope.loadingPage = false;

                            alertify.alert("<strong>Di-Gital web</strong> : " + ata.message);
                        });
                };
            }
            else{
                $state.go("404");
            }
        })


        .controller('ProduitController', function($rootScope, $scope, $location, $http, alertify, $state,
                                                  $timeout, $cookies, ROOT_URL, $log, $modal, DTOptionsBuilder,
                                                  DTColumnDefBuilder) {
            if($cookies.getObject('uData')) {

                $scope.api = $location.protocol() + "://" + $location.host() + ':' + $location.port() + "" + ROOT_URL.api;

                $scope.jwt_token = $cookies.getObject('jw_token');

                $scope.profileType = $cookies.getObject('uData').profile.profileType;


                $scope.loadingPage = false;

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
                    DTColumnDefBuilder.newColumnDef(5).notSortable()
                ];



                $scope.findAllData = function () {

                    $scope.loadingPage = true;

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-produit",

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
                                //$scope.existData = data.data.length > 0;
                            },
                            function(data, status, xhrStatus){
                                $scope.loadingPage = false;
                            });
                };
                $scope.findAllData();


                $scope.openFrmProduit = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/e-module/depot/service/produit/produit-frm.html',

                        controller: 'InstanceParametreProduit',

                        resolve: {
                            item: function () {
                                return item || null;
                            }
                        }
                    });

                    modalInstance.result.then(function (result) {
                        //console.log('result: ' + item);
                    }, function () {
                        $scope.findAllData();
                    });
                }


                $scope.actionOneItemProduit = function (id, item, action) {

                    if(action === 1){

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir supprimer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/delete-item-produit",

                                    method: "PUT",
                                    data: JSON.stringify(item),

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

                                        $scope.findAllData();

                                    }
                                    else {
                                        alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                    }

                                },function(data, status, headers, config){
                                    $scope.loadingPage = false;
                                    alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.message);
                                });
                            }
                        });
                    }
                    else{

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir activer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/active-item-produit",

                                    method: "PUT",
                                    data: JSON.stringify(item),

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

                                        $scope.findAllData();

                                    }
                                    else {
                                        alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                    }

                                },function(data, status, headers, config){
                                    $scope.loadingPage = false;
                                    alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                });
                            }
                        });
                    }

                };
            }
            else{
                $state.go("404");
            }
        })


        .controller('InstanceParametreProduit', function($scope, $modalInstance, item, $http, alertify, $location,
                                                         ROOT_URL, $state, $cookies, $log) {

            if($cookies.getObject('uData')){

                $scope.api                          = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token                    = $cookies.getObject('jw_token');

                $scope.api 			                = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.style 		                = "btn-primary";
                $scope.items 		                = {};
                $scope.nomBtn 		                = "Enregistrer";
                $scope.titleFrm                     = "Ajouter un nouveau produit";
                $scope.url                          = "create-new-produit";

                $scope.date                         = new Date();

                $scope.items.profile                = $cookies.getObject('uData').profile;
                $scope.items.seuilAlerte            = 6;


                $scope.items.prixVteEnGros          = 0;
                $scope.items.montantVenduEspere     = 0;
                $scope.items.prixVteDemiGros        = 0;
                $scope.items.prixVente              = 0;


                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };

                $scope.calcule = function(val) {
                    console.log(val)
                    $log.info(val)

                    if (val !== undefined && $scope.items.qteEnStock !== undefined){

                        $scope.items.montantVenduEspere     = parseInt(val) * parseInt($scope.items.qteEnStock);
                    }
                    else{

                        $scope.items.montantVenduEspere     = 0;
                    }
                };


                if (item !== null && item !=='') {

                    $scope.titleFrm = "Modifier informations sur la boisson n° "+item.ordre;

                    $scope.url = 'update-produit';

                    $scope.style = "btn-success";

                    $scope.nomBtn = "Appliquer"

                    $scope.items = item;
                }


                $scope.findAllCategorie = function () {

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api + '/liste-categorie-boisson',

                        method: "GET",

                        async: true,

                        cache: false,

                        headers: {

                            'Content-Type': 'application/json',

                            'Authorization': 'Bearer ' + $scope.jwt_token,

                            'Accept': 'application/json',
                        }

                    }).then(function (data) {
                        $scope.categorieList = data.data;
                    });
                }();


                $scope.saveProduit = function () {

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

                            if (data.data.information.requestCode === 201) {

                                $modalInstance.dismiss()
                            } else if (data.data.information.requestCode === 400) {
                                alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                            } else {
                                alertify.alert("<strong>Di-Gital web</strong> : Impossible de traiter votre requ&ecirc;te.");
                            }
                        }, function (data, status, headers, config) {
                            $scope.loadingPage = false;

                            alertify.alert("<strong>Di-Gital web</strong> : " + ata.message);
                        });
                };
            }
            else{
                $state.go("404");
            }
        })


        .controller('ReceptionFactureController', function($rootScope, $scope, $location, $http, alertify, $state,
                                                           $timeout, $cookies, ROOT_URL, $log, $modal, DTOptionsBuilder,
                                                           DTColumnDefBuilder) {
            if($cookies.getObject('uData')) {

                $scope.api = $location.protocol() + "://" + $location.host() + ':' + $location.port() + "" + ROOT_URL.api;

                $scope.jwt_token = $cookies.getObject('jw_token');


                $scope.loadingPage = false;

                $scope.datas = {};

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
                    DTColumnDefBuilder.newColumnDef(4).notSortable()
                ];



                $scope.findAllData = function () {

                    $scope.loadingPage = true;

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-reception-de-la-facture",

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
                                //$scope.existData = data.data.length > 0;
                            },
                            function(data, status, xhrStatus){
                                $scope.loadingPage = false;
                            });
                };
                $scope.findAllData();


                $scope.openFrmReceptionFacture = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/e-module/depot/service/reception-facture/reception-facture-frm.html',

                        controller: 'InstanceReceptionFacture',

                        resolve: {
                            item: function () {
                                return item || null;
                            }
                        }
                    });

                    modalInstance.result.then(function (result) {
                        //console.log('result: ' + item);
                    }, function () {
                        $scope.findAllData();
                    });
                }


                $scope.actionOneItemReceptionFacture = function (id, item, action) {

                    if(action === 1){

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir supprimer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/delete-item-reception-de-la-facture",

                                    method: "PUT",
                                    data: JSON.stringify(item),

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

                                        $scope.findAllData();

                                    }
                                    else {
                                        alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                    }

                                },function(data, status, headers, config){
                                    $scope.loadingPage = false;
                                    alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.message);
                                });
                            }
                        });
                    }
                    else{

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir activer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/active-item-reception-de-la-facture",

                                    method: "PUT",
                                    data: JSON.stringify(item),

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

                                        $scope.findAllData();

                                    }
                                    else {
                                        alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                    }

                                },function(data, status, headers, config){
                                    $scope.loadingPage = false;
                                    alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                });
                            }
                        });
                    }

                };
            }
            else{
                $state.go("404");
            }
        })


        .controller('InstanceReceptionFacture', function($scope, $modalInstance, item, $http, alertify, $location,
                                                                  ROOT_URL, $state, $cookies, $log) {

            if($cookies.getObject('uData')){

                $scope.api                  = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token            = $cookies.getObject('jw_token');

                $scope.api 			        = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.style 		        = "btn-primary";
                $scope.items 		        = {};
                $scope.nomBtn 		        = "Enregistrer";
                $scope.titleFrm             = "Ajouter une nouveelle facture";
                $scope.url                  = "create-new-reception-de-la-facture";
                $scope.donnees              = {};
                $scope.nbre                 = {};
                $scope.date                 = new Date();


                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };

                var chars2 = '9874563210';


                for (var j = 4; j > 0; --j)

                    $scope.donnees.ordre += chars2[Math.floor(Math.random() * chars2.length)];

                console.log($scope.donnees.ordre)



                $scope.statusList = [
                    {
                        value : "EN ATTENTE DE PAIEMENT"
                    },
                    {
                        value : "FACTURE RÈGLÉE"
                    },
                    {
                        value : "FACTURE RÈGLÉE PARTIELLEMENT"
                    }
                ];


                $scope.optionList = [
                    {
                        value : "OUI"
                    },
                    {
                        value : "NON"
                    }
                ];


                $scope.showNumero = function (param) {

                    if(param === 'OUI'){

                        $scope.numeroFournisseur = $scope.items.fournisseur.contact;
                    }
                    else{

                        $scope.numeroFournisseur = 0;
                    }
                }


                $scope.findAllFourn = function () {

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api + '/liste-fournisseur-by-profile/'+$cookies.getObject('uData').profile.idDigital,

                        method: "GET",

                        async: true,

                        cache: false,

                        headers: {

                            'Content-Type': 'application/json',

                            'Authorization': 'Bearer ' + $scope.jwt_token,

                            'Accept': 'application/json',
                        }

                    }).then(function (data) {
                        $scope.fournisseurList = data.data;
                    });
                }();


                if (item !== null && item !=='') {

                    $scope.titleFrm = "Modifier informations sur la categorie boisson n° "+item.ordre;

                    $scope.url = 'update-reception-de-la-facture';

                    $scope.style = "btn-success";

                    $scope.nomBtn = "Appliquer"

                    $scope.items = item;
                }


                //Ajout des colis
                $scope.articleList = [];

                $scope.addToList = function () {

                    $scope.cpte ++;

                    $scope.donnees.id   = $scope.cpte;

                    $scope.articleList.push($scope.donnees);

                    $scope.nbre.colis   = $scope.articleList.length;

                    $scope.donnees      = {};
                }


                $scope.retrierToList = function(item){

                    $scope.articleList.splice( $scope.articleList.indexOf(item), 1);
                }


                $scope.saveReceptionFacture = function () {

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

                            if (data.data.information.requestCode === 201) {

                                $modalInstance.dismiss()
                            } else if (data.data.information.requestCode === 400) {
                                alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                            } else {
                                alertify.alert("<strong>Di-Gital web</strong> : Impossible de traiter votre requ&ecirc;te.");
                            }
                        }, function (data, status, headers, config) {
                            $scope.loadingPage = false;

                            alertify.alert("<strong>Di-Gital web</strong> : " + ata.message);
                        });
                };
            }
            else{
                $state.go("404");
            }
        })

})();