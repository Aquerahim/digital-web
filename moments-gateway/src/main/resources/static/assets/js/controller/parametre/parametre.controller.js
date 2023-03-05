
(function() {

    'use strict';

    DiGital

        .controller('ParametreBaseCourrier', function ($rootScope, $scope, $location, $http, alertify, $state,
                                                   $timeout, $cookies, ROOT_URL, $log) {

            if($cookies.getObject('uData')){

                $scope.api              = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token        = $cookies.getObject('jw_token');

                $log.info('ParametreBaseCourrier');
            }
            else{
                $state.go("404");
            }
        })


        .controller('ParametrePaysAutorise', function ($rootScope, $scope, $location, $http, alertify, $state,
                                                       $timeout, $cookies, ROOT_URL, $log, $modal, DTOptionsBuilder,
                                                       DTColumnDefBuilder) {
            if($cookies.getObject('uData')){

                $scope.api              = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token        = $cookies.getObject('jw_token');

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



                $scope.findall = function () {

                    $scope.loadingPage = true;

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-pays-autorise",

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
                $scope.findall();


                $scope.openFrmPaysAutorise = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/parametre/parametre-base/pays-autorise/frm-pays-autorise.html',
                        controller: 'InstancePaysAutorise',
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

                $scope.actionOneItemPaysAutorise = function (id, item, action) {

                    if(action === 1){

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("&Ecirc;tes-vous s&ucirc;r de vouloir supprimer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/delete-item-pays-autorise",

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

                                        $scope.findall();

                                    }
                                    else {
                                        alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                    }

                                },function(data, status, headers, config){
                                    $scope.loadingPage = false;
                                    alertify.alert("<strong>Di-Gital web</strong> : " + data.data.message);
                                });
                            }
                        });
                    }
                    else{

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("&Ecirc;tes-vous s&ucirc;r de vouloir activer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/active-item-pays-autorise",

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

                                        $scope.findall();

                                    }
                                    else {
                                        alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                    }

                                },function(data, status, headers, config){
                                    $scope.loadingPage = false;
                                    alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
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


        .controller('InstancePaysAutorise', function($scope, $modalInstance, item, $http, alertify, $location,
                                                     ROOT_URL, $state, $cookies, $log) {

            if($cookies.getObject('uData')){

                $scope.api              = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token        = $cookies.getObject('jw_token');

                $scope.api 			= $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.style 		= "btn-primary";
                $scope.items 		= {};
                $scope.nomBtn 		= "Enregistrer";
                $scope.titleFrm     = "Ajouter un nouveau pays autorisé";
                $scope.url          = "create-new-pays-autorise";

                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };


                if (item !== null && item !=='') {

                    $scope.titleFrm = "Modifier informations sur un type de contrat";

                    $scope.url = 'update-pays-autorise';

                    $scope.style = "btn-success";

                    $scope.nomBtn = "Appliquer"

                    $scope.items = item;
                }


                $scope.save = function () {

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


        .controller('ParametreTypeMessage', function ($rootScope, $scope, $location, $http, alertify, $state,
                                                      $timeout, $cookies, ROOT_URL, $log, $modal, DTOptionsBuilder,
                                                      DTColumnDefBuilder) {
            if($cookies.getObject('uData')){

                $scope.api              = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token        = $cookies.getObject('jw_token');
                $scope.loadingPage      = false;

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
                    DTColumnDefBuilder.newColumnDef(3).notSortable()
                ];



                $scope.findall = function () {

                    $scope.loadingPage = true;

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-type-message",

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


                $scope.openFrmTypeMessage = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/parametre/parametre-base/type-message/frm-type-message.html',
                        controller: 'InstanceTypeMessage',
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


                $scope.actionOneItemTypeMessage = function (id, item, action) {

                    if(action === 1){
                        alertify.okBtn("Oui").cancelBtn("Non").confirm("&Ecirc;tes-vous s&ucirc;r de vouloir supprimer l'enregistrement selectionn&eacute; ?",function(event){
                            if(event) {
                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/delete-item-type-message",

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

                                        $scope.findall();

                                    }
                                    else {
                                        alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                    }

                                },function(data, status, headers, config){
                                    $scope.loadingPage = false;
                                    alertify.alert("<strong>Di-Gital web</strong> : " + data.data.message);
                                });
                            }
                        });
                    }
                    else{

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("&Ecirc;tes-vous s&ucirc;r de vouloir activer l'enregistrement selectionn&eacute; ?",function(event){
                            if(event) {
                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/active-item-type-message",

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

                                        $scope.findall();

                                    }
                                    else {
                                        alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                    }

                                },function(data, status, headers, config){
                                    $scope.loadingPage = false;
                                    alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
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


        .controller('InstanceTypeMessage', function($scope, $modalInstance, item, $http, alertify, $location,
                                                    ROOT_URL, $state, $cookies, $log) {

            if($cookies.getObject('uData')){

                $scope.api              = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token        = $cookies.getObject('jw_token');

                $scope.style 		= "btn-primary";
                $scope.items 		= {};
                $scope.nomBtn 		= "Enregistrer";
                $scope.titleFrm     = "Ajouter un nouveau type de message";
                $scope.url          = "create-new-type-message";

                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };


                if (item !== null && item !=='') {

                    $scope.titleFrm = "Modifier informations sur un type de message";

                    $scope.url = 'update-type-message';

                    $scope.style = "btn-success";

                    $scope.nomBtn = "Appliquer"

                    $scope.items = item;
                }


                $scope.save = function () {

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
                            alertify.alert("<strong>Di-Gital web</strong> : " + data.message);
                        });
                };
            }
            else{
                $state.go("404");
            }
        })


        .controller('ParametreTypeContrat', function ($rootScope, $scope, $location, $http, alertify, $state,
                                                            $timeout, $cookies, ROOT_URL, $log, $modal, DTOptionsBuilder,
                                                            DTColumnDefBuilder) {
            if($cookies.getObject('uData')){

                $scope.api              = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token        = $cookies.getObject('jw_token');

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
                    DTColumnDefBuilder.newColumnDef(3).notSortable()
                ];



                $scope.findall = function () {

                    $scope.loadingPage = true;

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-type-contrat",

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
                $scope.findall();


                $scope.openFrmTypeContrat = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/parametre/parametre-base/type-contrat/frm-type-contrat.html',
                        controller: 'InstanceTypeContrat',
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

                $scope.actionOneItem = function (id, item, action) {

                    if(action === 1){
                        alertify.okBtn("Oui").cancelBtn("Non").confirm("&Ecirc;tes-vous s&ucirc;r de vouloir supprimer l'enregistrement selectionn&eacute; ?",function(event){
                            if(event) {
                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/delete-item-type-contrat",

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

                                        $scope.findall();

                                    }
                                    else {
                                        alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                    }

                                },function(data, status, headers, config){
                                    $scope.loadingPage = false;
                                    alertify.alert("<strong>Di-Gital web</strong> : " + data.data.message);
                                });
                            }
                        });
                    }
                    else{

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("&Ecirc;tes-vous s&ucirc;r de vouloir activer l'enregistrement selectionn&eacute; ?",function(event){
                            if(event) {
                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/active-item-type-contrat",

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

                                        $scope.findall();

                                    }
                                    else {
                                        alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                    }

                                },function(data, status, headers, config){
                                    $scope.loadingPage = false;
                                    alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
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


        .controller('InstanceTypeContrat', function($scope, $modalInstance, item, $http, alertify, $location,
                                                        ROOT_URL, $state, $cookies, $log) {

            if($cookies.getObject('uData')){

                $scope.api              = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token        = $cookies.getObject('jw_token');

                $scope.style 		= "btn-primary";
                $scope.items 		= {};
                $scope.nomBtn 		= "Enregistrer";
                $scope.titleFrm     = "Ajouter un nouveau type de contrat";
                $scope.url          = "create-new-type-contrat";

                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };


                if (item !== null && item !=='') {

                    $scope.titleFrm = "Modifier informations sur un type de contrat";

                    $scope.url = 'update-type-contrat';

                    $scope.style = "btn-success";

                    $scope.nomBtn = "Appliquer"

                    $scope.items = item;
                }


                $scope.save = function () {

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
                            alertify.alert("<strong>Di-Gital web</strong> : " + data.message);
                        });
                };

            }
            else{
                $state.go("404");
            }
        })


        .controller('ParametreCivilite', function ($rootScope, $scope, $location, $http, alertify, $state,
                                                   $timeout, $cookies, ROOT_URL, $log, $modal, DTOptionsBuilder,
                                                   DTColumnDefBuilder) {
            if($cookies.getObject('uData')){

                $scope.api              = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token        = $cookies.getObject('jw_token');
                $scope.loadingPage 		= false;
                $scope.datas 			= {};

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



                $scope.findall = function () {

                    $scope.loadingPage = true;

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-civilite",

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
                $scope.findall();


                $scope.openFrmCivilite = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/parametre/parametre-base/civilite/frm-civilite.html',
                        controller: 'InstanceCivilite',
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


                $scope.actionOneItem = function (id, item, action) {

                    if(action === 1){
                        alertify.okBtn("Oui").cancelBtn("Non").confirm("&Ecirc;tes-vous s&ucirc;r de vouloir supprimer l'enregistrement selectionn&eacute; ?",function(event){
                            if(event) {
                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/delete-item-civilite",

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

                                        $scope.findall();

                                    }
                                    else {
                                        alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                    }

                                },function(data, status, headers, config){
                                    $scope.loadingPage = false;
                                    alertify.alert("<strong>Di-Gital web</strong> : " + data.data.message);
                                });
                            }
                        });
                    }
                    else{

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("&Ecirc;tes-vous s&ucirc;r de vouloir activer l'enregistrement selectionn&eacute; ?",function(event){
                            if(event) {
                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/active-item-civilite",

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

                                        $scope.findall();

                                    }
                                    else {
                                        alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                    }

                                },function(data, status, headers, config){
                                    $scope.loadingPage = false;
                                    alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
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


        .controller('InstanceCivilite', function($scope, $modalInstance, item, $http, alertify, $location,
                                                 ROOT_URL, $state, $cookies, $log) {
            if($cookies.getObject('uData')){

                $scope.api          = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token    = $cookies.getObject('jw_token');

                $scope.style 		= "btn-primary";
                $scope.items 		= {};
                $scope.nomBtn 		= "Enregistrer";
                $scope.titleFrm     = "Ajouter une nouvelle civilite";
                $scope.url          = "create-new-civilite";

                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };


                if (item !== null && item !=='') {

                    $scope.titleFrm = "Modifier informations sur la civilité";

                    $scope.url = 'update-civilite';

                    $scope.style = "btn-success";

                    $scope.nomBtn = "Appliquer"

                    $scope.items = item;
                }


                $scope.save = function () {

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
                            alertify.alert("<strong>Di-Gital web</strong> : " + data.message);
                        });
                };

            }
            else{
                $state.go("404");
            }
        })


        .controller('ParametreMotifSuspension', function ($rootScope, $scope, $location, $http, alertify, $state,
                                                            $timeout, $cookies, ROOT_URL, $log, $modal, DTOptionsBuilder,
                                                            DTColumnDefBuilder) {
            if($cookies.getObject('uData')){

                $scope.api              = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token        = $cookies.getObject('jw_token');

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
                    DTColumnDefBuilder.newColumnDef(3).notSortable()
                ];



                $scope.findall = function () {

                    $scope.loadingPage = true;

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-motif-suspension-collaboration",

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
                $scope.findall();


                $scope.openFrmMotifSuspension = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/parametre/parametre-base/motif/frm-motif-suspension-collaboration.html',
                        controller: 'InstanceMotifSuspension',
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


                $scope.actionOneItem = function (id, item, action) {

                    if(action === 1){
                        alertify.okBtn("Oui").cancelBtn("Non").confirm("&Ecirc;tes-vous s&ucirc;r de vouloir supprimer l'enregistrement selectionn&eacute; ?",function(event){
                            if(event) {
                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/delete-item-motif-suspension-collaboration",

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

                                        $scope.findall();

                                    }
                                    else {
                                        alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                    }

                                },function(data, status, headers, config){
                                    $scope.loadingPage = false;
                                    alertify.alert("<strong>Di-Gital web</strong> : " + data.data.message);
                                });
                            }
                        });
                    }
                    else{

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("&Ecirc;tes-vous s&ucirc;r de vouloir activer l'enregistrement selectionn&eacute; ?",function(event){
                            if(event) {
                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/active-item-motif-suspension-collaboration",

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

                                        $scope.findall();

                                    }
                                    else {
                                        alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                    }

                                },function(data, status, headers, config){
                                    $scope.loadingPage = false;
                                    alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
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


        .controller('InstanceMotifSuspension', function($scope, $modalInstance, item, $http, alertify, $location,
                                                      ROOT_URL, $state, $cookies, $log) {

            if($cookies.getObject('uData')){

                $scope.api              = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token        = $cookies.getObject('jw_token');

                $scope.style 		= "btn-primary";
                $scope.items 		= {};
                $scope.nomBtn 		= "Enregistrer";
                $scope.titleFrm     = "Ajouter un nouveau motif de suspension collaboration";
                $scope.url          = "create-new-motif-suspension-collaboration";

                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };

                if (item !== null && item !=='') {

                    $scope.titleFrm = "Modifier informations sur un motif de suspension collaboration";

                    $scope.url = 'update-motif-suspension-collaboration';

                    $scope.style = "btn-success";

                    $scope.nomBtn = "Appliquer"

                    $scope.items = item;
                }


                $scope.save = function () {

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
                            alertify.alert("<strong>Di-Gital web</strong> : " + data.message);
                        });
                };
            }
            else{
                $state.go("404");
            }
        })


        .controller('ParametreSmsCredential', function ($rootScope, $scope, $location, $http, alertify, $state,
                                                            $timeout, $cookies, ROOT_URL, $log, $modal, DTOptionsBuilder,
                                                            DTColumnDefBuilder) {
            if($cookies.getObject('uData')){

                $scope.api              = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token        = $cookies.getObject('jw_token');
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



                $scope.findall = function () {

                    $scope.loadingPage = true;

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-sms-credential",

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
                $scope.findall();


                $scope.openFrmSmsCredential = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/parametre/parametre-applicatif/sms-credential/frm-sms-credential.html',
                        controller: 'InstanceSmsCredential',
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


                $scope.actionOneItemSmsCredential = function (id, item, action) {

                    if(action === 1){
                        alertify.okBtn("Oui").cancelBtn("Non").confirm("&Ecirc;tes-vous s&ucirc;r de vouloir supprimer l'enregistrement selectionn&eacute; ?",function(event){
                            if(event) {
                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/delete-item-sms-credential",

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

                                        $scope.findall();

                                    }
                                    else {
                                        alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                    }

                                },function(data, status, headers, config){
                                    $scope.loadingPage = false;
                                    alertify.alert("<strong>Di-Gital web</strong> : " + data.data.message);
                                });
                            }
                        });
                    }
                    else{

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("&Ecirc;tes-vous s&ucirc;r de vouloir activer l'enregistrement selectionn&eacute; ?",function(event){
                            if(event) {
                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/active-item-sms-credential",

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

                                        $scope.findall();

                                    }
                                    else {
                                        alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                    }

                                },function(data, status, headers, config){
                                    $scope.loadingPage = false;
                                    alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
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


        .controller('InstanceSmsCredential', function($scope, $modalInstance, item, $http, alertify, $location,
                                                        ROOT_URL, $state, $cookies, $log) {

            if($cookies.getObject('uData')){

                $scope.api              = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token        = $cookies.getObject('jw_token');

                $scope.style 		= "btn-primary";
                $scope.items 		= {};
                $scope.nomBtn 		= "Enregistrer";
                $scope.titleFrm     = "Ajouter de nouveau acc&egrave;s sms souscripteur";
                $scope.url          = "create-new-sms-credential";

                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };

                $scope.items.ref = getRandomSpan(10);

                function getRandomSpan(length) {
                    var result           = '';
                    var characters       = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ01234567899874563210';
                    var charactersLength = characters.length;
                    for ( var i = 0; i < length; i++ ) {
                        result += characters.charAt(Math.floor(Math.random() * charactersLength));
                    }
                    return result;
                }


                if (item !== null && item !=='') {

                    $scope.titleFrm = "Modifier informations sur les acc&egrave;s sms souscripteur";

                    $scope.url = 'update-sms-credential';

                    $scope.style = "btn-success";

                    $scope.nomBtn = "Appliquer"

                    $scope.items = item;
                }


                $scope.saveCredential = function () {

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
                            alertify.alert("<strong>Di-Gital web</strong> : " + data.message);
                        });
                };
            }
            else{
                $state.go("404");
            }
        })


        .controller('ParametreCompagnieRoutiere', function ($rootScope, $scope, $location, $http, alertify, $state,
                                                   $timeout, $cookies, ROOT_URL, $log, $modal, DTOptionsBuilder,
                                                   DTColumnDefBuilder) {
            if($cookies.getObject('uData')){

                $scope.api              = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token        = $cookies.getObject('jw_token');

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
                    DTColumnDefBuilder.newColumnDef(7).notSortable()
                ];



                $scope.findall = function () {

                    $scope.loadingPage = true;

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-compagnie-routiere",

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
                $scope.findall();


                $scope.openFrmCompagnie = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/parametre/service-routier/compagnie-routiere/frm-compagnie-routiere.html',
                        controller: 'InstanceFromCompagnie',
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
                $state.go("404");
            }
        })


        .controller('InstanceFromCompagnie', function($scope, $modalInstance, item, $http, alertify, $location,
                                                          ROOT_URL, $state, $cookies, $log, Upload) {

            console.clear();

            if($cookies.getObject('uData')){

                $scope.api                      = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token                = $cookies.getObject('jw_token');

                $scope.style 		            = "btn-primary";
                $scope.items 		            = {};
                $scope.nomBtn 		            = "Enregistrer";
                $scope.titleFrm                 = "Ajouter une nouvelle entreprise";
                $scope.url                      = "create-new-compagnie-routiere";
                $scope.uploadLogo               = "upload-logo-compagnie-routiere";
                $scope.modification             = false;

                $scope.minDate                  = new Date();
                $scope.items.tauxCommLivreur    = 0;

                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };


                $scope.findAllTypeContrat = function () {

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api + '/find-all-type-contrat-disponible',

                        method: "GET",

                        async: true,

                        cache: false,

                        headers: {

                            'Content-Type': 'application/json',

                            'Authorization': 'Bearer ' + $scope.jwt_token,

                            'Accept': 'application/json',
                        }

                    }).then(function (data, status, headers, config) {
                            $scope.findAllTypeContrat = data.data;
                        },
                        function (data, status, headers, config) {
                            console.log(data);
                        });
                }();


                $scope.findAllDureeContrat = function () {

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api + '/find-all-duree-contrat',

                        method: "GET",

                        async: true,

                        cache: false,

                        headers: {

                            'Content-Type': 'application/json',

                            'Authorization': 'Bearer ' + $scope.jwt_token,

                            'Accept': 'application/json',
                        }

                    }).then(function (data, status, headers, config) {
                            $scope.findAllDureeContrat = data.data;
                        },
                        function (data, status, headers, config) {
                            console.log(data);
                        });
                }();


                $scope.findAllSmsCredential = function () {

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api + '/find-all-sms-credential-affected',

                        method: "GET",

                        async: true,

                        cache: false,

                        headers: {

                            'Content-Type': 'application/json',

                            'Authorization': 'Bearer ' + $scope.jwt_token,

                            'Accept': 'application/json',
                        }

                    }).then(function (data, status, headers, config) {
                            $scope.findAllSms = data.data;
                        },
                        function (data, status, headers, config) {
                            console.log(data);
                        });
                }();


                $scope.findAllPaysAutorise = function () {

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api + '/find-all-pays-autorise-disponible',

                        method: "GET",

                        async: true,

                        cache: false,

                        headers: {

                            'Content-Type': 'application/json',

                            'Authorization': 'Bearer ' + $scope.jwt_token,

                            'Accept': 'application/json',
                        }

                    }).then(function (data, status, headers, config) {
                            $scope.findAllPaysAutorise = data.data;
                        },
                        function (data, status, headers, config) {
                            console.log(data);
                        });
                }();


                $scope.findAllTypeSouscrivant = function () {

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api + '/find-all-type-souscrivant',

                        method: "GET",

                        async: true,

                        cache: false,

                        headers: {

                            'Content-Type': 'application/json',

                            'Authorization': 'Bearer ' + $scope.jwt_token,

                            'Accept': 'application/json',
                        }

                    }).then(function (data, status, headers, config) {
                            $scope.findAllTypeSouscrivant = data.data;
                        },
                        function (data, status, headers, config) {
                            console.log(data);
                        });
                }();


                if (item !== null && item !=='') {

                    $scope.titleFrm = "Modifier informations sur la compagnie";

                    $scope.url = 'update-compagnie-routiere';

                    $scope.style = "btn-success";

                    $scope.nomBtn = "Appliquer"

                    $scope.items = item;

                    $scope.modification = true;
                }


                $scope.saveCompagnie = function () {

                    $scope.loadingPage = true;

                    //$log.info("création d'une compagnie")

                    var iSize = ($scope.logo.size/1024/1024);

                    iSize = (Math.round(iSize * 100) / 100);

                    if(iSize > 2.00){

                        $scope.loadingPage = false;

                        alertify.alert("<strong>Di-Gital web</strong> : La taille maximale du fichier image a t&eacute;l&eacute;charger doit &ecirc;tre de 2 M&eacute;gaBits (MB)");
                    }
                    else{

                        $scope.uploadeFile($scope.logo);
                    }
                };


                $scope.uploadeFile = function (file) {

                    $scope.loadingPage = true;

                    //$log.info(file);

                    file.upload = Upload.upload({
                        url: $scope.api+'/'+$scope.uploadLogo,
                        data: {
                            file: file
                        }
                    });

                    file.upload.then(function (response) {

                        if(response.status === 200){
                            //Mise place de l'enregistrement en base des informations
                            $scope.items.logo = file.name;

                            $http({

                                url: $scope.api + '/' + $scope.url,

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

                                        $modalInstance.dismiss();

                                    } else if (data.data.information.requestCode === 400) {
                                        alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);

                                    } else {
                                        alertify.alert("<strong>Di-Gital web</strong> : Impossible de traiter votre requ&ecirc;te.");
                                    }

                                }, function (data, status, headers, config) {
                                    $scope.loadingPage = false;
                                    alertify.alert("<strong>Di-Gital web</strong> : " + "Impossible de faire la cr&eacute;ation de la compagnie");
                                });

                        }
                        else{
                            alertify.alert("<strong>Di-Gital web</strong> : " + response.data.message);
                        }

                    }, function (response) {
                        $scope.loadingPage = false;
                        console.log(response);
                    }, function (evt) {
                        file.progress = Math.min(100, parseInt(100.0 * evt.loaded / evt.total));
                    });
                };


                $scope.tab = 1;

                $scope.setTab = function(newTab){
                    $scope.tab = newTab;
                };

                $scope.isSet = function(tabNum){
                    return $scope.tab === tabNum;
                };


                $scope.updateCompagnie = function () {

                    $scope.loadingPage = true;

                    $log.info("update compagnie");

                    if (item !== null && item.id !== undefined) {

                        $http({

                            url: $scope.api + '/' + $scope.url,

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

                                if (data.data.information.requestCode === 201) {

                                    $modalInstance.dismiss();

                                } else if (data.data.information.requestCode === 400) {

                                    alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);

                                }
                                else {
                                    alertify.alert("<strong>Di-Gital web</strong> : Impossible de traiter votre requ&ecirc;te.");
                                }
                            }, function (data, status, headers, config) {
                                $scope.loadingPage = false;
                                alertify.alert("<strong>Di-Gital web</strong> : " + data.message);
                            });
                    }
                    else{
                        alertify.alert("<strong>Di-Gital web</strong> : Impossible de faire la mise à jour demandu&ecirc;e.");
                    }
                };
            }
            else{
                $state.go("404");
            }
        })


        .controller('ParametreGareRoutiere', function ($rootScope, $scope, $location, $http, alertify, $state,
                                                            $timeout, $cookies, ROOT_URL, $log, $modal, DTOptionsBuilder,
                                                            DTColumnDefBuilder) {
            console.clear();

            if($cookies.getObject('uData')){

                $scope.api              = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token        = $cookies.getObject('jw_token');

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
                    DTColumnDefBuilder.newColumnDef(6).notSortable()
                ];



                $scope.findall = function () {

                    $scope.loadingPage = true;

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-gare-routiere",

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
                $scope.findall();


                $scope.openFrmGareRoutiereCompagnie = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/parametre/service-routier/gare-routiere/frm-gare-routiere.html',
                        controller: 'InstanceGareRoutiereCompagnie',
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


                $scope.actionOneItemGareRoutiereCompagnie = function (id, item, action) {

                    if(action === 1){
                        alertify.okBtn("Oui").cancelBtn("Non").confirm("&Ecirc;tes-vous s&ucirc;r de vouloir supprimer l'enregistrement selectionn&eacute; ?",function(event){
                            if(event) {
                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/delete-item-gare-routiere",

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

                                        $scope.findall();

                                    }
                                    else {
                                        alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                    }

                                },function(data, status, headers, config){
                                    $scope.loadingPage = false;
                                    alertify.alert("<strong>Di-Gital web</strong> : " + data.data.message);
                                });
                            }
                        });
                    }
                    else{

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> : &Ecirc;tes-vous s&ucirc;r de vouloir activer l'enregistrement selectionn&eacute; ?",function(event){
                            if(event) {
                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/active-item-gare-routiere",

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

                                        $scope.findall();

                                    }
                                    else {
                                        alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                    }

                                },function(data, status, headers, config){
                                    $scope.loadingPage = false;
                                    alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
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


        .controller('InstanceGareRoutiereCompagnie', function($scope, $modalInstance, item, $http, alertify, $location,
                                                      ROOT_URL, $state, $cookies, $log) {

            if($cookies.getObject('uData')){

                console.clear();
                $log.info(" --------[ Instance Gare Routiere Compagnie ]--------")

                $scope.api              = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token        = $cookies.getObject('jw_token');

                $scope.style 		= "btn-primary";
                $scope.items 		= {};
                $scope.nomBtn 		= "Enregistrer";
                $scope.titleFrm     = "Ajouter un nouveau point de vente ou point relais";
                $scope.url          = "create-new-gare-routiere";

                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };


                if (item !== null && item !=='') {

                    $scope.titleFrm = "Modifier informations sur un point de vente ou point relais";

                    $scope.url = 'update-gare-routiere';

                    $scope.style = "btn-success";

                    $scope.nomBtn = "Appliquer";

                    $scope.items = item;
                }


                $scope.findAllCompagnie = function () {

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api + '/find-all-compagnie-routiere',

                        method: "GET",

                        async: true,

                        cache: false,

                        headers: {

                            'Content-Type': 'application/json',

                            'Authorization': 'Bearer ' + $scope.jwt_token,

                            'Accept': 'application/json',
                        }

                    }).then(function (data, status, headers, config) {
                            $scope.findAllCompagnie = data.data;
                        },
                        function (data, status, headers, config) {
                            $log.info(data);
                        });
                }();


                $scope.saveGareRoutiere = function () {

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

                            $scope.loading = false;

                            if (data.data.information.requestCode === 201) {

                                $modalInstance.dismiss()
                            }
                            else if ("<strong>Di-Gital web</strong> : " + data.data.information.requestCode === 409) {

                                alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);

                            } else {

                                alertify.alert("<strong>Di-Gital web</strong> : Impossible de traiter votre requête.");
                            }
                        }, function (data, status, headers, config) {
                            $scope.loading = false;
                            alertify.alert("<strong>Di-Gital web</strong> : " + data.message);
                        });
                };
            }
            else{
                $state.go("404");
            }
        })


        .controller('ParametreTypeSouscrivant', function ($rootScope, $scope, $location, $http, alertify, $state,
                                                          $timeout, $cookies, ROOT_URL, $log, $modal, DTOptionsBuilder,
                                                          DTColumnDefBuilder) {

            if($cookies.getObject('uData')){

                $scope.api              = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token        = $cookies.getObject('jw_token');

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
                    DTColumnDefBuilder.newColumnDef(3).notSortable()
                ];



                $scope.findall = function () {

                    $scope.loadingPage = true;

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-type-souscrivant",

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


                $scope.openFrmTypeSouscrivant = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/parametre/parametre-base/type-souscrivant/frm-type-souscrivant.html',
                        controller: 'InstanceTypeSouscrivant',
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


                $scope.actionOneItemTypeSouscrivant = function (id, item, action) {

                    if(action === 1){
                        alertify.okBtn("Oui").cancelBtn("Non").confirm("&Ecirc;tes-vous s&ucirc;r de vouloir supprimer l'enregistrement selectionn&eacute; ?",function(event){
                            if(event) {
                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/delete-item-type-souscrivant",

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

                                        $scope.findall();

                                    }
                                    else {
                                        alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                    }

                                },function(data, status, headers, config){
                                    $scope.loadingPage = false;
                                    alertify.alert("<strong>Di-Gital web</strong> : " + data.data.message);
                                });
                            }
                        });
                    }
                    else{

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> : &Ecirc;tes-vous s&ucirc;r de vouloir activer l'enregistrement selectionn&eacute; ?",function(event){
                            if(event) {
                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/active-item-type-souscrivant",

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

                                        $scope.findall();

                                    }
                                    else {
                                        alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                    }

                                },function(data, status, headers, config){
                                    $scope.loadingPage = false;
                                    alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
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

        .controller('InstanceTypeSouscrivant', function($scope, $modalInstance, item, $http, alertify, $location,
                                                        ROOT_URL, $state, $cookies, $log) {

            if($cookies.getObject('uData')){

                $scope.api              = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token        = $cookies.getObject('jw_token');

                $scope.style 		= "btn-primary";
                $scope.items 		= {};
                $scope.nomBtn 		= "Enregistrer";
                $scope.titleFrm     = "Ajouter un nouveau type de message";
                $scope.url          = "create-new-type-souscrivant";

                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };


                if (item !== null && item !=='') {

                    $scope.titleFrm = "Modifier informations sur un type de message";

                    $scope.url = 'update-type-souscrivant';

                    $scope.style = "btn-success";

                    $scope.nomBtn = "Appliquer"

                    $scope.items = item;
                }


                $scope.save = function () {

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

                            alertify.alert("<strong>Di-Gital web</strong> : " + data.message);
                        });
                };
            }
            else{
                $state.go("404");
            }
        })


        .controller('ParametreCompteUtilisateur', function ($rootScope, $scope, $location, $http, alertify, $state,
                                                   $timeout, $cookies, ROOT_URL, $log, $modal, DTOptionsBuilder,
                                                   DTColumnDefBuilder) {

            if($cookies.getObject('uData')){

                $scope.api              = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token        = $cookies.getObject('jw_token');

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
                    DTColumnDefBuilder.newColumnDef(6).notSortable()
                ];



                $scope.findall = function () {

                    $scope.loadingPage = true;

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-compte-utilisateur",

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
                $scope.findall();


                $scope.openFrmCompteUtilisateur = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/parametre/compte-utilisateur/frm-compte-utilisateur.html',

                        controller: 'InstanceCompteUtilisateur',

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


                $scope.actionOneItemCompteUtilisateur = function (id, item, action) {

                    if(action === 1){

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("&Ecirc;tes-vous s&ucirc;r de vouloir supprimer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {
                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/delete-item-compte-utilisateur",

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

                                        $scope.findall();

                                    }
                                    else {
                                        alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                    }

                                },function(data, status, headers, config){
                                    $scope.loadingPage = false;
                                    alertify.alert("<strong>Di-Gital web</strong> : " + data.data.message);
                                });
                            }
                        });
                    }
                    else{

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("&Ecirc;tes-vous s&ucirc;r de vouloir activer l'enregistrement selectionn&eacute; ?",function(event){
                            if(event) {
                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/active-item-compte-utilisateur",

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

                                        $scope.findall();

                                    }
                                    else {
                                        alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                    }

                                },function(data, status, headers, config){
                                    $scope.loadingPage = false;
                                    alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
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

        .controller('InstanceCompteUtilisateur', function($scope, $modalInstance, item, $http, alertify, $location,
                                                          ROOT_URL, $state, $cookies, $log) {

            if($cookies.getObject('uData')){
                
                $scope.jwt_token        = $cookies.getObject('jw_token');

                $scope.api 			= $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.style 		= "btn-primary";
                $scope.items 		= {};
                $scope.modificateur  = false;
                $scope.nomBtn 		= "Enregistrer";
                $scope.titleFrm     = "Ajouter un nouveau compte utilisateur";
                $scope.url          = "create-new-compte-utilisateur";
                $scope.minDate                  = new Date();

                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };


                if (item !== null && item !=='') {

                    $scope.modificateur  = true;

                    $scope.titleFrm = "Modifier informations sur le compte utilisateur";

                    $scope.url = 'update-compte-utilisateur';

                    $scope.style = "btn-success";

                    $scope.nomBtn = "Appliquer";


                    $scope.items.nomPrenoms      = item.profile.nomPrenoms;
                    $scope.profileType           = item.profile.profileType;
                    $scope.items.username        = item.username;
                    $scope.items.phone           = item.profile.phone;
                    $scope.items.idDigital       = item.profile.idDigital;
                }


                $scope.loadSelecBox = function (data, position) {

                    if(position === "T"){

                        if(data){

                            $http({

                                dataType: 'jsonp',

                                url: $scope.api + '/find-all-compagnie-routiere-type-souscrivant/'+data.id,

                                method: "GET",

                                async: true,

                                cache: false,

                                headers: {

                                    'Content-Type': 'application/json',

                                    'Authorization': 'Bearer ' + $scope.jwt_token,

                                    'Accept': 'application/json',
                                }

                            }).then(function (data, status, headers, config) {
                                    $scope.findAllSouscripteur = data.data;
                                },
                                function (data, status, headers, config) {
                                    $log.info(data);
                                });

                            /*if(data.id === 1){

                                $http({

                                    dataType: 'jsonp',

                                    url: $scope.api + '/find-all-compagnie-routiere-type-souscrivant/'+data.id,

                                    method: "GET",

                                    async: true,

                                    cache: false,

                                    headers: {

                                        'Content-Type': 'application/json',

                                        'Authorization': 'Bearer ' + $scope.jwt_token,

                                        'Accept': 'application/json',
                                    }

                                }).then(function (data, status, headers, config) {
                                        $scope.findAllSouscripteur = data.data;
                                    },
                                    function (data, status, headers, config) {
                                        $log.info(data);
                                    });
                            }
                            else{
                                alertify.alert("<strong>Di-Gital web</strong> : Aucun souscripteur disponible pour cette catégorie.");
                            }*/
                        }
                    }

                    if(position === "C"){

                        if(data){

                            $http({

                                dataType: 'jsonp',

                                url: $scope.api + '/find-all-gare-routiere-disponible-compagnie/'+data.id,

                                method: "GET",

                                async: true,

                                cache: false,

                                headers: {

                                    'Content-Type': 'application/json',

                                    'Authorization': 'Bearer ' + $scope.jwt_token,

                                    'Accept': 'application/json',
                                }

                            }).then(function (data, status, headers, config) {
                                    $scope.findAllRepresentant = data.data;
                                },
                                function (data, status, headers, config) {
                                    $log.info(data);
                                });
                        }
                    }
                };


                //$scope.modules = ['apple', 'orange', 'pear', 'naartjie'];

                $scope.listeModules = [
                    {
                        id: 1,
                        module: 'colis',
                        checked: false,
                        description: "Expédition & Réception de Colis"
                    },
                    {
                        id: 2,
                        module: 'pressing',
                        checked: false,
                        description: "Gestion de Pressing"
                    },
                    {
                        id: 3,
                        module: 'livraison',
                        checked: false,
                        description: "Gestion de Livraison"
                    },
                    {
                        id: 4,
                        module: 'voyage',
                        checked: false,
                        description: "Gestion de Ticket de voyage"
                    },
                    {
                        id: 5,
                        module: 'part',
                        checked: true,
                        description: "Produit devops Phonix Acces Ltd"
                    },
                    {
                        id: 6,
                        module: 'assurance',
                        checked: false,
                        description: "Assurance Vie & NonLife"
                    },
                    {
                        id: 7,
                        module: 'exam',
                        checked: false,
                        description: "Prépa Exam"
                    },
                    {
                        id: 8,
                        module: 'depotBossion',
                        checked: false,
                        description: "Gestion de Depot de boisson"
                    },
                ];

                $scope.handleRadioClick = function (value) {
                    $log.info(value)
                };

                $scope.findTypeSouscripteur = function () {

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api + '/find-all-type-souscrivant-disponible',

                        method: "GET",

                        async: true,

                        cache: false,

                        headers: {

                            'Content-Type': 'application/json',

                            'Authorization': 'Bearer ' + $scope.jwt_token,

                            'Accept': 'application/json',
                        }

                    }).then(function (data, status, headers, config) {
                            $scope.findTypeSouscripteur = data.data;
                        },
                        function (data, status, headers, config) {
                            $log.info(data);
                        });
                }();


                //console.log($cookies.getObject('uData').profile.profileType)

                if($cookies.getObject('uData').profile.profileType === 'ADMIN_PART'){

                    $scope.profileTypeList = [
                        { value : "DIRECTEUR GENERAL", key: "CEO" },
                        { value : "COMPTABLE", key: "COMPTABILITE"  },
                        { value : "ADMINISTRATEUR PARTENAIRE", key: "ADMIN_PART" },
                        { value : "GESTIONNAIRE", key: "GESTIONNAIRE"  },
                        { value : "RESPONSABLE", key: "MANAGER"  },
                        //{ value : "PARTENAIRE D'AFFAIRE", key: "PARTENAIRE"  },
                    ];

                }
                else if($cookies.getObject('uData').profile.profileType === 'EXPERT' || $cookies.getObject('uData').profile.profileType === 'ADMIN'){

                    $scope.profileTypeList = [
                        { value : "ADMINISTRATEUR SUPRA", key: "EXPERT"  },
                        { value : "DIRECTEUR GENERAL", key: "CEO" },
                        { value : "DIRECTEUR GENERAL ADJOINT", key: "COO"  },
                        { value : "COMPTABLE", key: "COMPTABILITE"  },
                        { value : "ADMINISTRATEUR LOCAL", key: "ADMIN" },
                        { value : "ADMINISTRATEUR PARTENAIRE", key: "ADMIN_PART" },
                        { value : "GESTIONNAIRE", key: "GESTIONNAIRE"  },
                        { value : "RESPONSABLE", key: "MANAGER"  },
                        /*{ value : "PARTENAIRE D'AFFAIRE", key: "PARTENAIRE"  },
                        { value : "CHEF DE GARE", key: "MANAGER"  },
                        { value : "CHARGE CLIENTÈL", key: "GESTIONNAIRE"  },
                        { value : "CHARGE CLIENTÈL - SERVICE COURRIER", key: "COURRIER"  },
                        { value : "CHARGE CLIENTÈL - SERVICE VOYAGE", key: "TICKETING"  }*/
                    ];
                }



                $scope.generateUserName = function (typeProfile, compagnie) {

                    var result = '';

                    var chars2 = '9874563210';

                    if (typeProfile) {

                        $scope.timeoutPwd = "120 jours soit 4 Mois";

                        for (var j = 4; j > 0; --j) result += chars2[Math.floor(Math.random() * chars2.length)];

                        switch (typeProfile) {
                            case "CEO":	//ceo
                                $scope.items.username = compagnie + "-" + result + "D1";
                                break;
                            case "COO":	//coo
                                $scope.items.username = compagnie + "-" + result + "DI";
                                break;
                            case "ADMIN":
                                $scope.items.username = "ADM" + compagnie;
                                break;
                            case "ADMIN_PART":
                                $scope.items.username = "ADMIN-PART" + result;
                                break;
                            case "EXPERT":
                                $scope.items.username = "ADMINISTRATEUR";
                                break;
                            case "COMPTABILITE":
                                $scope.items.username = compagnie + "-" + result+ "CO";
                                break;
                            case "COURRIER":	//Gestionnaire
                                $scope.items.username = compagnie + "-" + result + "GE";
                                break;
                            case "TICKETING":	//manager
                                $scope.items.username = compagnie + "-" + result + "GE";
                                break;
                            case "GESTIONNAIRE":	//manager
                                $scope.items.username = compagnie + "-" + result + "GE";
                                break;
                            case "MANAGER":	//manager
                                $scope.items.username = compagnie + "-"+ result + "MA";
                                break;
                            case "PARTENAIRE":	//Partenaire
                                $scope.items.username = compagnie + "-"+ result + "PA";
                                break;
                        }
                        // $log.info($scope.items.username);
                    }
                };


                $scope.createUserAccount = function () {

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

                            $scope.loading = false;

                            if (data.data.information.requestCode === 201) {

                                $modalInstance.dismiss()
                            } else if (data.data.information.requestCode === 409) {
                                alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);

                            } else {
                                alertify.alert("<strong>Di-Gital web</strong> : Impossible de traiter votre requête.");
                            }
                        }, function (data, status, headers, config) {
                            $scope.loading = false;
                            alertify.alert("<strong>Di-Gital web</strong> : " + data.message);
                        });
                };
                
            }
            else{
                $state.go("404");
            }
        })


        .controller('ParametreServiceCommercialisable', function ($rootScope, $scope, $location, $http, alertify, $state,
                                                   $timeout, $cookies, ROOT_URL, $log, $modal, DTOptionsBuilder,
                                                   DTColumnDefBuilder) {

            if($cookies.getObject('uData')){

                $scope.api              = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token        = $cookies.getObject('jw_token');

                $log.info('Parametre pour service commercialisable');
            }
            else{
                $state.go("404");
            }
            
        })

        .controller('ParametreServiceCourrier', function ($rootScope, $scope, $location, $http, alertify, $state,
                                                   $timeout, $cookies, ROOT_URL, $log, $modal, DTOptionsBuilder,
                                                   DTColumnDefBuilder) {

            if($cookies.getObject('uData')){

                $scope.api              = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token        = $cookies.getObject('jw_token');

                $log.info('Parametre pour service courrier');
            }
            else{
                $state.go("404");
            }
        })


        .controller('ParametreSuiviDemande', function ($rootScope, $scope, $location, $http, alertify, $state,
                                                       $timeout, $cookies, ROOT_URL, $log, $modal, DTOptionsBuilder,
                                                       DTColumnDefBuilder) {
            if($cookies.getObject('uData')){

                $scope.api              = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token        = $cookies.getObject('jw_token');
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
                    DTColumnDefBuilder.newColumnDef(3).notSortable()
                ];



                $scope.findall = function () {

                    $scope.loadingPage = true;

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-suivi-demande",

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


                $scope.openFrmTypeMessage = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/parametre/parametre-base/suivi-demande/frm-suivi-demande.html',
                        controller: 'InstanceSuiviDemande',
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


                $scope.actionOneItemTypeMessage = function (id, item, action) {

                    if(action === 1){
                        alertify.okBtn("Oui").cancelBtn("Non").confirm("&Ecirc;tes-vous s&ucirc;r de vouloir supprimer l'enregistrement selectionn&eacute; ?",function(event){
                            if(event) {
                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/delete-item-suivi-demande",

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

                                        $scope.findall();

                                    }
                                    else {
                                        alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                    }

                                },function(data, status, headers, config){
                                    $scope.loadingPage = false;
                                    alertify.alert("<strong>Di-Gital web</strong> : " + data.data.message);
                                });
                            }
                        });
                    }
                    else{

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("&Ecirc;tes-vous s&ucirc;r de vouloir activer l'enregistrement selectionn&eacute; ?",function(event){
                            if(event) {
                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/active-item-suivi-demande",

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

                                        $scope.findall();

                                    }
                                    else {
                                        alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                    }

                                },function(data, status, headers, config){
                                    $scope.loadingPage = false;
                                    alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
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


        .controller('InstanceSuiviDemande', function($scope, $modalInstance, item, $http, alertify, $location,
                                                     ROOT_URL, $state, $cookies, $log) {

            if($cookies.getObject('uData')){

                $scope.api              = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token        = $cookies.getObject('jw_token');

                $scope.style 		= "btn-primary";
                $scope.items 		= {};
                $scope.nomBtn 		= "Enregistrer";
                $scope.titleFrm     = "Ajouter un nouveau suivi de demande";
                $scope.url          = "create-new-suivi-demande";

                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };


                if (item !== null && item !=='') {

                    $scope.titleFrm = "Modifier informations sur un suivi de demande";

                    $scope.url = 'update-suivi-demande';

                    $scope.style = "btn-success";

                    $scope.nomBtn = "Appliquer"

                    $scope.items = item;
                }


                $scope.save = function () {

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

                            alertify.alert("<strong>Di-Gital web</strong> : " + data.message);
                        });
                };
            }
            else{
                $state.go("404");
            }
        })

        .controller('ProfilController', function ($rootScope, $scope, $location, $http, alertify, $state,
                                                          $timeout, $cookies, ROOT_URL, $log) {

            console.clear();

            $scope.datas   = $cookies.getObject('uData');

            if($scope.datas) {

                $scope.api          = $location.protocol() + "://" + $location.host() + ':' + $location.port() + "" + ROOT_URL.api;

                $scope.jwt_token    = $cookies.getObject('jw_token');

                $scope.items        = {};

                $scope.mxDate      = new Date();


                $scope.items.idDigital    = $scope.datas.profile.idDigital;

                $scope.items.email        = $scope.datas.profile.email;

                $scope.items.nomPrenoms   = $scope.datas.profile.nomPrenoms;

                $scope.items.phone        = $scope.datas.profile.phone;

                $scope.items.birthdate    = $scope.datas.profile.birthdate;

                $scope.items.instagram    = $scope.datas.profile.instagram;

                $scope.items.facebook     = $scope.datas.profile.facebook;

                $scope.items.twitter      = $scope.datas.profile.twitter;

                $scope.items.skype        = $scope.datas.profile.skype;

                $scope.items.username     = $scope.datas.username;



                $scope.backTo = function () {

                    $state.go("mainpage.dashboard");
                }


                $scope.updateProfile = function () {

                    $scope.loadingPage = true;

                    $http({

                        url: $scope.api + '/update-profile',

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

                        if (data.status === 200 && data.xhrStatus === 'complete') {

                            $cookies.remove('uData');

                            var date = new Date();

                            date.setTime(date.getTime() + (180* 60 * 1000));

                            $cookies.putObject('uData', data.data,  {'expires' : date});

                            $state.go("mainpage.parametre.profil");
                        }
                        else{

                            alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                        }

                    }, function (data) {

                        $scope.loadingPage = false;

                        alertify.alert("<strong>Di-Gital web</strong> : " + data.message);
                    });
                };

            }
            else{

                $state.go("404");
            }
        })




        .controller('ParametreUtilisateurController', function ($rootScope, $scope, $location, $http, alertify, $state,
                                                  $timeout, $cookies, ROOT_URL, $log, AuthenticationService) {

            console.clear();

            $scope.datas   = $cookies.getObject('uData');

            $scope.jwt_token    = $cookies.getObject('jw_token');

            //$log.info($cookies.getObject('uData'))

            if($scope.datas && $scope.jwt_token) {

                $scope.api          = $location.protocol() + "://" + $location.host() + ':' + $location.port() + "" + ROOT_URL.api;



                $scope.items        = {};

                $scope.mxDate      = new Date();

                $scope.viewBtn     = true;

                $scope.required    = false;


                $scope.items.idDigital    = $scope.datas.profile.idDigital;

                $scope.items.email        = $scope.datas.profile.email;

                $scope.items.nomPrenoms   = $scope.datas.profile.nomPrenoms;

                $scope.items.phone        = $scope.datas.profile.phone;

                $scope.items.birthdate    = $scope.datas.profile.birthdate;

                $scope.items.instagram    = $scope.datas.profile.instagram;

                $scope.items.facebook     = $scope.datas.profile.facebook;

                $scope.items.twitter      = $scope.datas.profile.twitter;

                $scope.items.skype        = $scope.datas.profile.skype;

                //$scope.viewModule   = $scope.datas.profile.module;

                //$scope.datas.profile;

                $scope.backTo = function () {

                    $state.go("mainpage.dashboard");
                }


                $scope.getNewCodeOTP = function () {

                    //Vérifions si le mot de passe est identique
                    $scope.loadingPage = true;

                    if($scope.items.password !== $scope.items.confPassword){

                        $scope.loadingPage = false;

                        alertify.alert("<strong>Di-Gital web</strong> : Impossible de faire la demande d'un Code One-Time Password (OTP) car la confirmation du mot de passe est diff&eacute;rent du mot de passe saisie.");
                    }
                    else{

                        //Vérifions si le mot de passe respect la politique de l'entreprise
                        //$log.info($scope.items.confPassword)

                        var reg = new RegExp("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\$%\^&\*])(?=.{8,})");

                        //$log.info(reg.test($scope.items.confPassword))

                        if (reg.test($scope.items.confPassword) === true) {

                            return AuthenticationService

                                .codeUpdatePwd($scope.datas.username.toUpperCase(), $scope.api, $scope.jwt_token)

                                .then(

                                    function(response) {

                                        $scope.loadingPage = false;

                                        if(parseInt(response.status) === 200){

                                            $scope.viewBtn      = false;

                                            $scope.required     = true;

                                            $scope.items.codeOTP = response.data.codeOTP;
                                        }
                                        else {

                                            $scope.viewBtn = true;

                                            alertify.alert("<strong>Di-Gital web</strong> : " + response.data.information.message);
                                        }
                                    },
                                    function (response) {
                                        $scope.loadingPage = false;
                                        alertify.alert("<strong>Di-Gital web</strong> - TIME OUT : Connexion impossible pri&egrave;re verifier votre connexion ou vos identifiants de connexion.");
                                        $log.info(response)
                                    }
                                );
                        }
                        else{

                            $scope.loadingPage = false;

                            alertify.alert("<strong>Di-Gital web</strong> : Impossible de faire la demande d'un Code One-Time Password (OTP) car la structure du mot de passe ne respect pas celui de l'entreprise.");
                        }
                    }
                }

            }
            else{

                $state.go("404");
            }
        })






        .controller('OffreEntrepriseController', function ($rootScope, $scope, $location, $http, alertify, $state,
                                                   $timeout, $cookies, ROOT_URL, $log, $modal, DTOptionsBuilder,
                                                   DTColumnDefBuilder) {
            if($cookies.getObject('uData')){

                $scope.api              = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token        = $cookies.getObject('jw_token');
                $scope.loadingPage 		= false;
                $scope.datas 			= {};

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

                        url: $scope.api+"/find-all-offre-entreprise-sms",

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
                $scope.findall();


                $scope.openFrmOffreEntreprise = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/parametre/parametre-base/offre/frm-offre-entreprise-sms.html',
                        controller: 'InstanceOffreEntreprise',
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


                $scope.actionOneItem = function (id, item, action) {

                    if(action === 1){
                        alertify.okBtn("Oui").cancelBtn("Non").confirm("&Ecirc;tes-vous s&ucirc;r de vouloir supprimer l'enregistrement selectionn&eacute; ?",function(event){
                            if(event) {
                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/delete-item-offre-entreprise-sms",

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

                                        $scope.findall();

                                    }
                                    else {
                                        alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                    }

                                },function(data, status, headers, config){
                                    $scope.loadingPage = false;
                                    alertify.alert("<strong>Di-Gital web</strong> : " + data.data.message);
                                });
                            }
                        });
                    }
                    else{

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("&Ecirc;tes-vous s&ucirc;r de vouloir activer l'enregistrement selectionn&eacute; ?",function(event){
                            if(event) {
                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/active-item-offre-entreprise-sms",

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

                                        $scope.findall();

                                    }
                                    else {
                                        alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                    }

                                },function(data, status, headers, config){
                                    $scope.loadingPage = false;
                                    alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
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


        .controller('InstanceOffreEntreprise', function($scope, $modalInstance, item, $http, alertify, $location,
                                                 ROOT_URL, $state, $cookies, $log) {
            if($cookies.getObject('uData')){

                $scope.api          = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token    = $cookies.getObject('jw_token');

                $scope.style 		= "btn-primary";
                $scope.items 		= {};
                $scope.nomBtn 		= "Enregistrer";
                $scope.titleFrm     = "Ajouter une nouvelle offre sms pour entreprise";
                $scope.url          = "create-new-offre-entreprise-sms";

                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };


                if (item !== null && item !=='') {

                    $scope.titleFrm = "Modifier informations sur la civilité";

                    $scope.url = 'update-offre-entreprise-sms';

                    $scope.style = "btn-success";

                    $scope.nomBtn = "Appliquer"

                    $scope.items = item;
                }


                $scope.items.typeFormule = "Offre entreprise";

                $scope.calculateTaxe = function(amount) {

                    if (amount !== undefined){

                        $scope.items.taxeApplique   = parseInt(amount * 0.03);

                        $scope.items.totalPayer     = amount + parseInt($scope.items.taxeApplique);
                    }
                };


                $scope.save = function () {

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
                            alertify.alert("<strong>Di-Gital web</strong> : " + data.message);
                        });
                };

            }
            else{
                $state.go("404");
            }
        })
    ;
})();