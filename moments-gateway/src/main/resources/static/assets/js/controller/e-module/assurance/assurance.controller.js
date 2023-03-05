(function() {

    'use strict';

    DiGital

        .controller('ProspectController', function($rootScope, $scope, $location, $http, alertify, $state,
                                                     $timeout, $cookies, ROOT_URL, $log, $modal, DTOptionsBuilder,
                                                     DTColumnDefBuilder) {

            console.clear();

            if($cookies.getObject('uData') && $cookies.getObject('uData').profile.idDigital) {

                $scope.api          = $location.protocol() + "://" + $location.host() + ':' + $location.port() + "" + ROOT_URL.api;

                $scope.jwt_token    = $cookies.getObject('jw_token');

                //$scope.entrepriseId = $cookies.getObject('uData').profile.gareRoutiere.compagnie.id;

                $scope.profileId    = $cookies.getObject('uData').profile.idDigital;


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
                    DTColumnDefBuilder.newColumnDef(7).notSortable()
                ];

                //$log.info($cookies.getObject('uData').profile)
                //$log.info($cookies.getObject('uData').profile.gareRoutiere.compagnie.id)


                $scope.findallProspect = function () {

                    $scope.loadingPage = true;

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-prospect/"+$scope.profileId,

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
                $scope.findallProspect();


                $scope.openFrPorteFeuilleProspect = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/e-module/assurance/prospect/portefeuille-from-prospect.html',

                        controller: 'InstancePorteFeuilleProspect',

                        resolve: {
                            item: function () {
                                return item || null;
                            }
                        }
                    });

                    modalInstance.result.then(function (result) {
                        console.log('result: ' + item);
                    }, function () {
                        $scope.findallProspect();
                    });
                }


                $scope.openFrPorteFeuilleListeProspect = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/e-module/assurance/prospect/import-portefeuille-frm-prospect.html',

                        controller: 'InstancePorteFeuilleProspectList',

                        resolve: {
                            item: function () {
                                return item || null;
                            }
                        }
                    });

                    modalInstance.result.then(function (result) {
                        console.log('result: ' + item);
                    }, function () {
                        $scope.findallProspect();
                    });
                }


                $scope.actionOneItemPorteFeuilleProspect = function (id, item, action) {

                    if(action === 1){

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir supprimer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/delete-item-mes-prospect",

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

                                        $scope.findallProspect();

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

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir activer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/active-item-mes-prospect",

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

                                        $scope.findallProspect();

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


        .controller('InstancePorteFeuilleProspectList', function($scope, $modalInstance, item, $http, alertify, $location,
                                                                 ROOT_URL, $state, $cookies, $log, Upload) {
            console.clear();

            if($cookies.getObject('uData')) {

                $scope.api          = $location.protocol() + "://" + $location.host() + ':' + $location.port() + "" + ROOT_URL.api;

                $scope.jwt_token    = $cookies.getObject('jw_token');

                $scope.profileId    = $cookies.getObject('uData').profile.idDigital;

                $scope.fermer = function() {

                    $modalInstance.dismiss('cancel');
                };

                $scope.donwloadProspectFile = function () {

                    $scope.loadingPage = true;

                    var iSize = ($scope.file.size / 1024 / 1024);

                    iSize = (Math.round(iSize * 100) / 100);

                    if (iSize > 3.00) {

                        $scope.loadingPage = false;
                        alertify.alert("La taille maximale du fichier excel a importe doit être de 3 MegaBits (MB)");

                    }
                    else {
                        $scope.uploaderProspectFile($scope.file);
                    }
                };


                $scope.uploaderProspectFile = function (file) {

                    file.upload = Upload.upload({

                        url: $scope.api + '/importe-porte-feuille-prospect-fichier/'+$scope.profileId,

                        headers: {

                            'Content-Type': 'application/json',

                            'Authorization': 'Bearer ' + $scope.jwt_token,

                            'Accept': 'application/json',
                        },

                        data: {
                            file: file
                        }
                    });

                    file.upload.then(function (response) {

                        $scope.loadingPage = false;

                        if (response.data.statut === 201) {

                            alertify.okBtn("OK").alert(response.data.message, function (event) {
                                $modalInstance.dismiss();
                            });
                            $modalInstance.dismiss();
                        } else {
                            alertify.alert(response.data.message);
                        }
                    }, function (response) {
                        $scope.loadingPage = false;
                        alertify.alert(response.data.message);

                    }, function (evt) {

                        file.progress = Math.min(100, parseInt(100.0 * evt.loaded / evt.total));
                    });
                };
            }
            else{
                $state.go("login");
            }
        })


        .controller('InstancePorteFeuilleProspect', function($scope, $modalInstance, item, $http, alertify, $location,
                                                             ROOT_URL, $state, $cookies, $log) {
            if($cookies.getObject('uData')){

                $scope.api              = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token        = $cookies.getObject('jw_token');
                $scope.style 		    = "btn-primary";
                $scope.items 		    = {};
                $scope.nomBtn 		    = "Enregistrer";
                $scope.titleFrm         = "Ajouter une nouveau prospect dans le porte-feuille";
                $scope.url              = "create-new-prospect-in-porte-feuille";
                $scope.actived          = true;
                $scope.items.profile    = $cookies.getObject('uData').profile;
                $scope.items.entreprises = $cookies.getObject('uData').profile.gareRoutiere.compagnie;
                $scope.modif            = false;


                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };



                if (item !== null && item !=='') {

                    $scope.titleFrm = "Modifier informations sur un type de zone";

                    $scope.url = 'update-mes-prospect';

                    $scope.style = "btn-success";

                    $scope.nomBtn = "Appliquer"

                    $scope.items = item;
                }


                $scope.findAllTypeProspect = function () {

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api + '/find-all-type-client-assurance',

                        method: "GET",

                        async: true,

                        cache: false,

                        headers: {

                            'Content-Type': 'application/json',

                            'Authorization': 'Bearer ' + $scope.jwt_token,

                            'Accept': 'application/json',
                        }

                    }).then(function (data) {
                        $scope.findAllTypeProspect = data.data;
                    });
                }();


                $scope.findAllProduits = function () {

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api + '/find-all-produit-assurance',

                        method: "GET",

                        async: true,

                        cache: false,

                        headers: {

                            'Content-Type': 'application/json',

                            'Authorization': 'Bearer ' + $scope.jwt_token,

                            'Accept': 'application/json',
                        }

                    }).then(function (data) {
                        $scope.findAllProduits = data.data;
                    });
                }();


                $scope.findAllGrps = function () {

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api + '/find-all-groupe-prospect/'+$cookies.getObject('uData').profile.idDigital,

                        method: "GET",

                        async: true,

                        cache: false,

                        headers: {

                            'Content-Type': 'application/json',

                            'Authorization': 'Bearer ' + $scope.jwt_token,

                            'Accept': 'application/json',
                        }

                    }).then(function (data) {
                        $scope.findAllGrps = data.data;
                    });
                }();


                $scope.saveProspect = function () {

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

                            $modalInstance.dismiss()
                        }
                        else if (data.data.requestCode === 400) {
                            alertify.alert("<strong>Di-Gital web</strong> : " + data.data.message);

                        }
                        else {

                            alertify.alert("<strong>Di-Gital web</strong> : Impossible de traiter votre requ&ecirc;te.");
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

        .controller('PorteFeuilleClientController', function($rootScope, $scope, $location, $http, alertify, $state,
                                                     $timeout, $cookies, ROOT_URL, $log, $modal, DTOptionsBuilder,
                                                     DTColumnDefBuilder) {

            console.clear();

            if($cookies.getObject('uData') && $cookies.getObject('uData').profile.idDigital) {

                $scope.api          = $location.protocol() + "://" + $location.host() + ':' + $location.port() + "" + ROOT_URL.api;

                $scope.jwt_token    = $cookies.getObject('jw_token');

                //$scope.entrepriseId = $cookies.getObject('uData').profile.gareRoutiere.compagnie.id;

                $scope.profileId    = $cookies.getObject('uData').profile.idDigital;


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
                    DTColumnDefBuilder.newColumnDef(7).notSortable()
                ];

                //$log.info($cookies.getObject('uData').profile)
                //$log.info($cookies.getObject('uData').profile.gareRoutiere.compagnie.id)


                $scope.findallPorteFeuille = function () {

                    $scope.loadingPage = true;

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-portefeuille-client/"+$scope.profileId,

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
                $scope.findallPorteFeuille();


                $scope.openFrPorteFeuilleClients = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/e-module/assurance/client/portefeuille-frm-client.html',

                        controller: 'InstancePorteFeuilleClients',

                        resolve: {
                            item: function () {
                                return item || null;
                            }
                        }
                    });

                    modalInstance.result.then(function (result) {
                        console.log('result: ' + item);
                    }, function () {
                        $scope.findallPorteFeuille();
                    });
                }


                $scope.openFrPorteFeuilleListeClients = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/e-module/assurance/client/import-portefeuille-frm-client.html',

                        controller: 'InstancePorteFeuilleClientsList',

                        resolve: {
                            item: function () {
                                return item || null;
                            }
                        }
                    });

                    modalInstance.result.then(function (result) {
                        console.log('result: ' + item);
                    }, function () {
                        $scope.findallPorteFeuille();
                    });
                }


                $scope.actionOneItemPorteFeuilleClients = function (id, item, action) {

                    if(action === 1){

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir supprimer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/delete-item-mes-clients",

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

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir activer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/active-item-mes-clients",

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


        .controller('InstancePorteFeuilleClientsList', function($scope, $modalInstance, item, $http, alertify, $location,
                                                   ROOT_URL, $state, $cookies, $log, Upload) {

            console.clear();

            if($cookies.getObject('uData')) {

                $scope.api          = $location.protocol() + "://" + $location.host() + ':' + $location.port() + "" + ROOT_URL.api;

                $scope.jwt_token    = $cookies.getObject('jw_token');

                $scope.profileId    = $cookies.getObject('uData').profile.idDigital;

                $scope.fermer = function() {

                    $modalInstance.dismiss('cancel');
                };

                $scope.donwloadFile = function () {

                    $scope.loadingPage = true;

                    var iSize = ($scope.file.size / 1024 / 1024);

                    iSize = (Math.round(iSize * 100) / 100);

                    if (iSize > 3.00) {

                        $scope.loadingPage = false;
                        alertify.alert("La taille maximale du fichier excel a importe doit être de 3 MegaBits (MB)");

                    }
                    else {
                        $scope.uploaderFile($scope.file);
                    }
                };


                $scope.uploaderFile = function (file) {

                    file.upload = Upload.upload({

                        url: $scope.api + '/importe-porte-feuille-client-fichier/'+$scope.profileId,

                        headers: {

                            'Content-Type': 'application/json',

                            'Authorization': 'Bearer ' + $scope.jwt_token,

                            'Accept': 'application/json',
                        },

                        data: {
                            file: file
                        }
                    });

                    file.upload.then(function (response) {

                        $scope.loadingPage = false;

                        if (response.data.statut === 201) {

                            alertify.okBtn("OK").alert(response.data.message, function (event) {
                                $modalInstance.dismiss();
                            });
                            $modalInstance.dismiss();
                        } else {
                            alertify.alert(response.data.message);
                        }
                    }, function (response) {
                        $scope.loadingPage = false;
                        alertify.alert(response.data.message);

                    }, function (evt) {

                        file.progress = Math.min(100, parseInt(100.0 * evt.loaded / evt.total));
                    });
                };
            }
            else{
                $state.go("login");
            }
        })


        .controller('InstancePorteFeuilleClients', function($scope, $modalInstance, item, $http, alertify, $location,
                                                   ROOT_URL, $state, $cookies, $log) {

            if($cookies.getObject('uData')){

                $scope.api              = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token        = $cookies.getObject('jw_token');
                $scope.style 		    = "btn-primary";
                $scope.items 		    = {};
                $scope.nomBtn 		    = "Enregistrer";
                $scope.titleFrm         = "Ajouter une nouveau client dans le porte-feuille";
                $scope.url              = "create-new-client-in-porte-feuille";
                $scope.actived          = true;
                $scope.items.gare       = $cookies.getObject('uData').profile.gareRoutiere;
                $scope.items.profile    = $cookies.getObject('uData').profile;
                $scope.gare             = $scope.items.gare.gareRoutiere;
                $scope.compagnie        = $scope.items.gare.compagnie.compagnie;
                $scope.abbrev           = $scope.items.gare.compagnie.abbrev;
                $scope.modif            = false;


                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };



                $scope.findAllCivilite = function () {

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api + '/find-all-civilite',

                        method: "GET",

                        async: true,

                        cache: false,

                        headers: {

                            'Content-Type': 'application/json',

                            'Authorization': 'Bearer ' + $scope.jwt_token,

                            'Accept': 'application/json',
                        }

                    }).then(function (data) {
                        $scope.findAllCiviliteListe = data.data;
                    });
                }();


                $scope.findAllTypeClient = function () {

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api + '/find-all-type-client-assurance',

                        method: "GET",

                        async: true,

                        cache: false,

                        headers: {

                            'Content-Type': 'application/json',

                            'Authorization': 'Bearer ' + $scope.jwt_token,

                            'Accept': 'application/json',
                        }

                    }).then(function (data) {
                        $scope.findAllTypeClientListe = data.data;
                    });
                }();


                $scope.findAllProduit = function () {

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api + '/find-all-produit-assurance',

                        method: "GET",

                        async: true,

                        cache: false,

                        headers: {

                            'Content-Type': 'application/json',

                            'Authorization': 'Bearer ' + $scope.jwt_token,

                            'Accept': 'application/json',
                        }

                    }).then(function (data) {
                        $scope.findAllProduitListe = data.data;
                    });
                }();



                $scope.handleRadio = function (value) {
                    $log.info(value)
                };



                if (item !== null && item !=='') {

                    $scope.titleFrm = "Modifier informations Porte feuille";

                    $scope.url = 'update-mes-clients';

                    $scope.style = "btn-success";

                    $scope.nomBtn = "Appliquer"

                    $scope.items = item;
                }


                $scope.savePorteFeuille = function () {

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
                        }
                        else if (data.data.information.requestCode === 400) {
                            alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);

                        }
                        else {

                            alertify.alert("<strong>Di-Gital web</strong> : Impossible de traiter votre requ&ecirc;te.");
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


        .controller('TypeClientAssuranceController', function($rootScope, $scope, $location, $http, alertify, $state,
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



                $scope.findallTypeClientAssurance = function () {

                    $scope.loadingPage = true;

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-type-client-assurance",

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
                $scope.findallTypeClientAssurance();


                $scope.openFrmTypeClientAssurance = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/e-module/assurance/parametre/type-client-assurance-form.html',
                        controller: 'InstanceTypeClientAssurance',
                        resolve: {
                            item: function () {
                                return item || null;
                            }
                        }
                    });

                    modalInstance.result.then(function (result) {
                        console.log('result: ' + item);
                    }, function () {
                        $scope.findallTypeClientAssurance();
                    });
                }


                $scope.actionOneItemTypeClientAssurance = function (id, item, action) {

                    if(action === 1){

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir supprimer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/delete-item-type-client-assurance",

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

                                        $scope.findallTypeClientAssurance();

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

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir activer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/active-item-type-client-assurance",

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

                                        $scope.findallTypeClientAssurance();

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


        .controller('InstanceTypeClientAssurance', function($scope, $modalInstance, item, $http, alertify, $location,
                                                         ROOT_URL, $state, $cookies, $log) {

            console.clear();

            if($cookies.getObject('uData')){

                $scope.api                  = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token            = $cookies.getObject('jw_token');

                $scope.api 			        = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.style 		        = "btn-primary";
                $scope.items 		        = {};
                $scope.nomBtn 		        = "Enregistrer";
                $scope.titleFrm             = "Ajouter un nouveau type client";
                $scope.url                  = "create-new-type-client-assurance";
                /*$scope.items.entreprises    = $cookies.getObject('uData').profile.gareRoutiere.compagnie;
                $scope.profileType          = $cookies.getObject('uData').profile.profileType;*/

                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };



                if (item !== null && item !=='') {

                    $scope.titleFrm = "Modifier informations sur un type de client";

                    $scope.url = 'update-type-client-assurance';

                    $scope.style = "btn-success";

                    $scope.nomBtn = "Appliquer"

                    $scope.items = item;
                }


                $scope.createdTypeClient = function () {

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

                                $modalInstance.dismiss();

                            } else if (data.data.information.requestCode === 400) {

                                alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                            }
                            else {
                                alertify.alert("<strong>Di-Gital web</strong> : Impossible de traiter votre requ&ecirc;te.");
                            }
                        }, function (data, status, headers, config) {

                            $scope.loadingPage = false;

                            alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                        });
                };

            }
            else{
                $state.go("404");
            }
        })


        .controller('DateNotificationController', function($rootScope, $scope, $location, $http, alertify, $state,
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
                    .withOption('lengthMenu', [10, 50, 150, 250, 300])
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



                $scope.findallDateNotification = function () {

                    $scope.loadingPage = true;

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-date-de-notification",

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
                $scope.findallDateNotification();


                $scope.openFrmDateNotification = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/e-module/assurance/parametre/date-notification-form.html',
                        controller: 'InstanceDateNotification',
                        resolve: {
                            item: function () {
                                return item || null;
                            }
                        }
                    });

                    modalInstance.result.then(function (result) {
                        console.log('result: ' + item);
                    }, function () {
                        $scope.findallDateNotification();
                    });
                }


                $scope.actionOneItemDateNotification = function (id, item, action) {

                    if(action === 1){

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir supprimer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/delete-item-date-de-notification",

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

                                        $scope.findallDateNotification();

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

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir activer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/active-item-date-de-notification",

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

                                        $scope.findallDateNotification();

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


        .controller('InstanceDateNotification', function($scope, $modalInstance, item, $http, alertify, $location,
                                                         ROOT_URL, $state, $cookies) {

            console.clear();

            if($cookies.getObject('uData')){

                $scope.api                  = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token            = $cookies.getObject('jw_token');

                $scope.api 			        = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.style 		        = "btn-primary";
                $scope.items 		        = {};
                $scope.nomBtn 		        = "Enregistrer";
                $scope.titleFrm             = "Ajouter une nouvelle Date notification";
                $scope.url                  = "create-new-date-de-notification";

                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };



                if (item !== null && item !=='') {

                    $scope.titleFrm = "Modifier informations sur une Date notification";

                    $scope.url = 'update-date-de-notification';

                    $scope.style = "btn-success";

                    $scope.nomBtn = "Appliquer"

                    $scope.items = item;
                }


                $scope.createdDateNotification = function () {

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

                                $modalInstance.dismiss();

                            } else if (data.data.information.requestCode === 400) {

                                alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                            }
                            else {
                                alertify.alert("<strong>Di-Gital web</strong> : Impossible de traiter votre requ&ecirc;te.");
                            }
                        }, function (data, status, headers, config) {

                            $scope.loadingPage = false;

                            alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                        });
                };

            }
            else{
                $state.go("404");
            }
        })


        .controller('ProduitAssuranceController', function($rootScope, $scope, $location, $http, alertify, $state,
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


                $scope.findallProduitAssurance = function () {

                    $scope.loadingPage = true;

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-produit-assurance",

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
                $scope.findallProduitAssurance();

                $scope.openFrmProduitAssurance = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/e-module/assurance/parametre/produit-assurance-form.html',
                        controller: 'InstanceProduitAssurance',
                        resolve: {
                            item: function () {
                                return item || null;
                            }
                        }
                    });

                    modalInstance.result.then(function (result) {
                        console.log('result: ' + item);
                    }, function () {
                        $scope.findallProduitAssurance();
                    });
                }


                $scope.actionOneItemProduitAssurance = function (id, item, action) {

                    if(action === 1){

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir supprimer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/delete-item-produit-assurance",

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

                                        $scope.findallProduitAssurance();

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

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir activer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/active-item-produit-assurance",

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

                                        $scope.findallProduitAssurance();

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


        .controller('InstanceProduitAssurance', function($scope, $modalInstance, item, $http, alertify, $location,
                                                         ROOT_URL, $state, $cookies, $log) {

            console.clear();

            if($cookies.getObject('uData')){

                $scope.api                  = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token            = $cookies.getObject('jw_token');

                $scope.api 			        = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.style 		        = "btn-primary";
                $scope.items 		        = {};
                $scope.nomBtn 		        = "Enregistrer";
                $scope.titleFrm             = "Ajouter un nouveau slogan";
                $scope.url                  = "create-new-produit-assurance";

                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };


                if (item !== null && item !=='') {

                    $scope.titleFrm = "Modifier informations sur un slogan";

                    $scope.url = 'update-produit-assurance';

                    $scope.style = "btn-success";

                    $scope.nomBtn = "Appliquer"

                    $scope.items = item;
                }


                $scope.createdProduitAssurance = function () {

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

                                $modalInstance.dismiss();

                            } else if (data.data.information.requestCode === 400) {

                                alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                            }
                            else {
                                alertify.alert("<strong>Di-Gital web</strong> : Impossible de traiter votre requ&ecirc;te.");
                            }
                        }, function (data, status, headers, config) {

                            $scope.loadingPage = false;

                            alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                        });
                };

            }
            else{
                $state.go("404");
            }
        })

        .controller('ParametreModelMessageController', function($rootScope, $scope, $location, $http, alertify, $state,
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



                $scope.findall = function () {

                    $scope.loadingPage = true;

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-modele-message-assurance",

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


                $scope.openFrmModelMSg = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/e-module/assurance/parametre/modele-message-form.html',
                        controller: 'InstanceParametreModelMessage',
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


                $scope.actionOneItemModelMSg = function (id, item, action) {

                    if(action === 1){

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir supprimer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/delete-item-modele-message-assurance",

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

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir activer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/active-item-modele-message-assurance",

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


        .controller('InstanceParametreModelMessage', function($scope, $modalInstance, item, $http, alertify, $location,
                                                 ROOT_URL, $state, $cookies, $log) {

            if($cookies.getObject('uData')){

                $scope.api                  = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token            = $cookies.getObject('jw_token');

                $scope.api 			        = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.style 		        = "btn-primary";
                $scope.items 		        = {};
                $scope.nomBtn 		        = "Enregistrer";
                $scope.titleFrm             = "Ajouter une nouveau modèle de message d'assurance";
                $scope.url                  = "create-new-modele-message-assurance";

                //$log.info($cookies.getObject('uData').profile.module);

                $scope.items.entreprises    = $cookies.getObject('uData').profile.gareRoutiere.compagnie;
                $scope.profileType          = $cookies.getObject('uData').profile.profileType;
                $scope.module               = $cookies.getObject('uData').profile.module;


                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };


                $scope.findAllEntreprises = function () {

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

                    }).then(function (data) {
                        $scope.findAllEntreprises = data.data;
                    });
                }();


                $scope.findAllTypeMessageList = function () {

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api + '/find-all-type-message-assurance-by-module/'+$scope.module,

                        method: "GET",

                        async: true,

                        cache: false,

                        headers: {

                            'Content-Type': 'application/json',

                            'Authorization': 'Bearer ' + $scope.jwt_token,

                            'Accept': 'application/json',
                        }

                    }).then(function (data) {

                        $scope.findAllTypeMessageList = data.data;
                    });
                }();


                if (item !== null && item !=='') {

                    $scope.titleFrm = "Modifier informations sur un type de zone";

                    $scope.url = 'update-modele-message-assurance';

                    $scope.style = "btn-success";

                    $scope.nomBtn = "Appliquer"

                    $scope.items = item;
                }


                $scope.saveModelMessage = function () {

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



        .controller('ParametreSloganEntrepriseController', function($rootScope, $scope, $location, $http, alertify, $state,
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



                $scope.findallSloganEntreprise = function () {

                    $scope.loadingPage = true;

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-slogan-entreprise",

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
                $scope.findallSloganEntreprise();


                $scope.openFrmSloganEntreprise = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/e-module/assurance/parametre/slogan-entreprise-form.html',
                        controller: 'InstanceSloganEntreprise',
                        resolve: {
                            item: function () {
                                return item || null;
                            }
                        }
                    });

                    modalInstance.result.then(function (result) {
                        console.log('result: ' + item);
                    }, function () {
                        $scope.findallSloganEntreprise();
                    });
                }


                $scope.actionOneItemSloganEntreprise = function (id, item, action) {

                    if(action === 1){

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir supprimer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/delete-item-slogan-entreprise",

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

                                        $scope.findallSloganEntreprise();

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

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir activer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/active-item-slogan-entreprise",

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

                                        $scope.findallSloganEntreprise();

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


        .controller('InstanceSloganEntreprise', function($scope, $modalInstance, item, $http, alertify, $location,
                                                 ROOT_URL, $state, $cookies, $log) {

            console.clear();

            if($cookies.getObject('uData')){

                $scope.api                  = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token            = $cookies.getObject('jw_token');

                $scope.api 			        = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.style 		        = "btn-primary";
                $scope.items 		        = {};
                $scope.nomBtn 		        = "Enregistrer";
                $scope.titleFrm             = "Ajouter un nouveau slogan";
                $scope.url                  = "create-new-slogan-entreprise";
                $scope.items.entreprises    = $cookies.getObject('uData').profile.gareRoutiere.compagnie;
                $scope.profileType          = $cookies.getObject('uData').profile.profileType;

                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };


                //$scope.entreprise = $cookies.getObject('uData').profile.gareRoutiere.compagnie.compagnie;

                $scope.findEntreprise = function () {

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

                    }).then(function (data) {
                        $scope.findEntrepriseList = data.data;
                    });
                }();



                if (item !== null && item !=='') {

                    $scope.titleFrm = "Modifier informations sur un slogan";

                    $scope.url = 'update-slogan-entreprise';

                    $scope.style = "btn-success";

                    $scope.nomBtn = "Appliquer"

                    $scope.items = item;
                }


                $scope.created = function () {

                    $scope.loadingPage = true;

                    //$log.info('Je rentre');

                    //$log.info($scope.items)

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

                            $modalInstance.dismiss();

                        } else if (data.data.information.requestCode === 400) {

                            alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                        }
                        else {
                            alertify.alert("<strong>Di-Gital web</strong> : Impossible de traiter votre requ&ecirc;te.");
                        }
                    }, function (data, status, headers, config) {

                        $scope.loadingPage = false;

                        alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                    });
                };

            }
            else{
                $state.go("404");
            }
        })


        .controller('TypeMessageAssuranceController', function($rootScope, $scope, $location, $http, alertify, $state,
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



                $scope.findallTypeMessageAssurance = function () {

                    $scope.loadingPage = true;

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-type-message-assurance",

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
                $scope.findallTypeMessageAssurance();


                $scope.openFrmTypeMessageAssurance = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/e-module/assurance/parametre/type-message-assurance-form.html',
                        controller: 'InstanceTypeMessageAssurance',
                        resolve: {
                            item: function () {
                                return item || null;
                            }
                        }
                    });

                    modalInstance.result.then(function (result) {
                        console.log('result: ' + item);
                    }, function () {
                        $scope.findallTypeMessageAssurance();
                    });
                }


                $scope.actionOneItemTypeMessageAssurance = function (id, item, action) {

                    if(action === 1){

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir supprimer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/delete-item-type-message-assurance",

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

                                        $scope.findallTypeMessageAssurance();

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

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir activer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/active-item-type-message-assurance",

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

                                        $scope.findallTypeMessageAssurance();

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


        .controller('InstanceTypeMessageAssurance', function($scope, $modalInstance, item, $http, alertify, $location,
                                                             ROOT_URL, $state, $cookies, $log) {

            console.clear();

            if($cookies.getObject('uData')){

                $scope.api                  = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token            = $cookies.getObject('jw_token');

                $scope.api 			        = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.style 		        = "btn-primary";
                $scope.items 		        = {};
                $scope.nomBtn 		        = "Enregistrer";
                $scope.titleFrm             = "Ajouter un nouveau type client";
                $scope.url                  = "create-new-type-message-assurance";
                /*$scope.items.entreprises    = $cookies.getObject('uData').profile.gareRoutiere.compagnie;
                $scope.profileType          = $cookies.getObject('uData').profile.profileType;*/

                //$log.info($cookies.getObject('uData').profile)

                $scope.items.module         = $cookies.getObject('uData').profile.module;

                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };



                if (item !== null && item !=='') {

                    $scope.titleFrm = "Modifier informations sur un type de client";

                    $scope.url = 'update-type-message-assurance';

                    $scope.style = "btn-success";

                    $scope.nomBtn = "Appliquer"

                    $scope.items = item;
                }


                $scope.createdTypeClient = function () {

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

                                $modalInstance.dismiss();

                            } else if (data.data.information.requestCode === 400) {

                                alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                            }
                            else {
                                alertify.alert("<strong>Di-Gital web</strong> : Impossible de traiter votre requ&ecirc;te.");
                            }
                        }, function (data, status, headers, config) {

                            $scope.loadingPage = false;

                            alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                        });
                };

            }
            else{
                $state.go("404");
            }
        })


        .controller('GroupeController', function($rootScope, $scope, $location, $http, alertify, $state,
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
                    .withOption('lengthMenu', [10, 50, 150, 250, 300])
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



                $scope.findallGroupe = function () {

                    $scope.loadingPage = true;

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-groupe",

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
                $scope.findallGroupe();


                $scope.openFrmGroupe = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/e-module/assurance/parametre/groupe-form.html',
                        controller: 'InstanceGroupe',
                        resolve: {
                            item: function () {
                                return item || null;
                            }
                        }
                    });

                    modalInstance.result.then(function (result) {
                        console.log('result: ' + item);
                    }, function () {
                        $scope.findallGroupe();
                    });
                }


                $scope.actionOneItemGroupe = function (id, item, action) {

                    if(action === 1){

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir supprimer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/delete-item-groupe",

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

                                        $scope.findallGroupe();

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

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir activer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/active-item-groupe",

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

                                        $scope.findallGroupe();

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


        .controller('InstanceGroupe', function($scope, $modalInstance, item, $http, alertify, $location,
                                               ROOT_URL, $state, $cookies) {

            console.clear();

            if($cookies.getObject('uData')){

                $scope.api                  = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token            = $cookies.getObject('jw_token');

                $scope.api 			        = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.style 		        = "btn-primary";
                $scope.items 		        = {};
                $scope.nomBtn 		        = "Enregistrer";
                $scope.titleFrm             = "Ajouter un nouveau groupe";
                $scope.url                  = "create-new-groupe";

                $scope.items.profile        = $cookies.getObject('uData').profile;

                $scope.items.entreprises    = $cookies.getObject('uData').profile.gareRoutiere.compagnie;

                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };



                if (item !== null && item !=='') {

                    $scope.titleFrm                 = "Modifier informations sur un Groupe";

                    $scope.url                      = 'update-groupe';

                    $scope.style                    = "btn-success";

                    $scope.nomBtn                   = "Appliquer"

                    $scope.items                    = item;

                    $scope.items.profileId          = $cookies.getObject('uData').profile.idDigital;
                }


                $scope.createdGroupe = function () {

                    $scope.loadingPage = true;

                    var route = $scope.api + '/' + $scope.url;

                    var method = "POST";

                    if (item !== null && item.ordre !== undefined) {

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

                                $modalInstance.dismiss();

                            } else if (data.data.information.requestCode === 400) {

                                alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                            }
                            else {
                                alertify.alert("<strong>Di-Gital web</strong> : Impossible de traiter votre requ&ecirc;te.");
                            }
                        }, function (data, status, headers, config) {

                            $scope.loadingPage = false;

                            alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                        });
                };

            }
            else{
                $state.go("404");
            }
        })


        .controller('NotificationMessageMasseController', function($rootScope, $scope, $location, $http, alertify, $state,
                                                 $timeout, $cookies, ROOT_URL) {

            if($cookies.getObject('uData')) {

                $scope.api                      = $location.protocol() + "://" + $location.host() + ':' + $location.port() + "" + ROOT_URL.api;

                $scope.jwt_token                = $cookies.getObject('jw_token');

                $scope.profileId                = $cookies.getObject('uData').profile.idDigital

                $scope.loadingPage              = false;

                $scope.datas                    = {};

                $scope.date                     = new Date();

                $scope.datas.entreprises        = $cookies.getObject('uData').profile.gareRoutiere.compagnie;

                $scope.datas.profile            = $cookies.getObject('uData').profile;

                $scope.loadData = function (param, index) {

                    if (index === "0"){

                        if(param !== undefined){

                            if(param === 'PROSPECT') {

                                $scope.loadProspectGrpe = function () {

                                    $http({

                                        dataType: 'jsonp',

                                        url: $scope.api+"/find-all-groupe-prospect/"+$scope.profileId,

                                        method: "GET",

                                        async: true,

                                        cache: false,

                                        headers: {

                                            'Content-Type': 'application/json',

                                            'Authorization': 'Bearer ' + $scope.jwt_token,

                                            'Accept': 'application/json',
                                        }
                                    }).then(function (data) {
                                        $scope.loadProspectGrpe = data.data;
                                    });
                                }();
                            }
                        }
                        else {

                            alertify.alert("<strong>Di-Gital web</strong> Veuillez d&eacute;finir la source de donn&eacute;es svp.");
                        }
                    }

                    if (index === "1"){

                        if(param !== undefined){

                            if(param === 'OUI') {

                                var typeHash;

                                if($scope.datas.typeMessage === 'NOUVEL-AN'){

                                    typeHash = "NOUVEL AN"
                                }

                                if($scope.datas.typeMessage === 'NOUVEAU-PRODUIT'){

                                    typeHash = "NOUVEAU PRODUIT"
                                }

                                if($scope.datas.typeMessage === 'OPERATION-COMMERCIALE'){

                                    typeHash = "OPÉRATION COMMERCIALE"
                                }

                                if($scope.datas.typeMessage === 'OPERATION-MARKETING'){

                                    typeHash = "OPÉRATION COMMERCIALE"
                                }

                                if($scope.datas.typeMessage === 'FETE-RELIGIEUSE'){

                                    typeHash = "FÊTE CIVILE ET RELIGIEUSE"
                                }

                                if($scope.datas.typeMessage === 'FETE-MERE'){

                                    typeHash = "FÊTE DES MÈRES"
                                }

                                if($scope.datas.typeMessage === 'FETE-PERE'){

                                    typeHash = "FÊTE DU PÈRE"
                                }

                                $scope.showMsg = function () {

                                    $http({

                                        dataType: 'jsonp',

                                        url: $scope.api+"/find-one-modele-mesage/"+$scope.profileId+"/"+typeHash,

                                        method: "GET",

                                        async: true,

                                        cache: false,

                                        headers: {

                                            'Content-Type': 'application/json',

                                            'Authorization': 'Bearer ' + $scope.jwt_token,

                                            'Accept': 'application/json',
                                        }
                                    }).then(function (data) {

                                        if(data.data == null){

                                            alertify.alert("<strong>Di-Gital web</strong> Aucun modèle d emessage n'est défini pour ce menu.");
                                        }
                                        else{
                                            $scope.datas.message = data.data.message;
                                        }
                                    });
                                }();
                            }
                            else{

                                $scope.datas.message = "";
                            }
                        }
                        else {

                            alertify.alert("<strong>Di-Gital web</strong> Votre choix compte afin d'avoir une meilleure exp&eacute;rence client.");
                        }
                    }
                }


                $scope.saveCampagneNewYear = function () {

                    $scope.loadingPage = true;

                    $http({

                        url: $scope.api + '/create-new-campagne-message-en-masse',

                        method: "POST",

                        data: angular.toJson($scope.datas),

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

                                $state.reload();

                                alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : La programmation a &eacute;t&eacute; effectu&eacute;e avec succ&egrave;s.",function(event){

                                    if(event) {

                                        $state.go("mainpage.list-notification-message-en-masse");
                                    }
                                });

                                //$window.location.reload();

                            } else if (data.data.requestCode === 400) {

                                alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                            }
                            else {
                                alertify.alert("<strong>Di-Gital web</strong> : Impossible de traiter votre requ&ecirc;te.");
                            }

                        }, function (data, status, headers, config) {

                            $scope.loadingPage = false;

                            alertify.alert("<strong>Di-Gital web</strong> : Un soucis dans l'objet de pour le mapping des donn&eacute;es");
                        });
                };


                $scope.goToBack = function() {
                    $state.go("mainpage.dashboard");
                };

            }
            else{
                $state.go("404");
            }
        })

        .controller('NotificationListMessageMasseController', function($rootScope, $scope, $location, $http, alertify, $state,
                                                                       $timeout, $cookies, ROOT_URL, $log, $modal, DTOptionsBuilder,
                                                                       DTColumnDefBuilder) {

            if($cookies.getObject('uData')) {

                $scope.api = $location.protocol() + "://" + $location.host() + ':' + $location.port() + "" + ROOT_URL.api;

                $scope.jwt_token = $cookies.getObject('jw_token');

                $scope.profileId = $cookies.getObject('uData').profile.idDigital

                $scope.loadingPage = false;

                $scope.datas = {};

                $scope.dtOptions = DTOptionsBuilder
                    .newOptions()
                    .withBootstrap("responsive",!0)
                    .withOption('order', [[0, 'asc']])
                    .withOption('lengthMenu', [10, 50, 150, 250, 300])
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



                $scope.findNotificationList = function () {

                    $scope.loadingPage = true;

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-campagne-message-en-masse/"+$scope.profileId,

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
                $scope.findNotificationList();


                $scope.openFrmNotificationList = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/e-module/assurance/message/message-form.html',
                        controller: 'InstanceNotificationList',
                        resolve: {
                            item: function () {
                                return item || null;
                            }
                        }
                    });

                    modalInstance.result.then(function (result) {
                        console.log('result: ' + item);
                    }, function () {
                        $scope.findallGroupe();
                    });
                }


                $scope.actionOneItemNotificationList = function (id, item, action) {

                    if(action === 1){

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir supprimer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/delete-item-campagne-message-en-masse",

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

                                        $scope.findallGroupe();

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

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir activer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/active-item-campagne-message-en-masse",

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

                                        $scope.findallGroupe();

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


        .controller('InstanceNotificationList', function($scope, $modalInstance, item, $http, alertify, $location,
                                               ROOT_URL, $state, $cookies) {

            console.clear();

            if($cookies.getObject('uData')){

                $scope.style 		        = "btn-primary";
                $scope.items 		        = {};

                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };



                if (item !== null && item !=='') {

                    $scope.titleFrm          = "Informations sur la notification en masse";

                    $scope.items             = item;
                }


            }
            else{
                $state.go("404");
            }
        })

        .controller('MessageProgrammeController', function($rootScope, $scope, $location, $http, alertify, $state,
                                                                       $timeout, $cookies, ROOT_URL, $log, $modal, DTOptionsBuilder,
                                                                       DTColumnDefBuilder) {

            if($cookies.getObject('uData')) {

                $scope.api = $location.protocol() + "://" + $location.host() + ':' + $location.port() + "" + ROOT_URL.api;

                $scope.jwt_token    = $cookies.getObject('jw_token');

                $scope.profileId    = $cookies.getObject('uData').profile.idDigital

                $scope.loadingPage  = false;

                $scope.datas = {};

                $scope.dtOptions = DTOptionsBuilder
                    .newOptions()
                    .withBootstrap("responsive",!0)
                    .withOption('order', [[0, 'asc']])
                    .withOption('lengthMenu', [10, 50, 150, 250, 300])
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



                $scope.findMsgProgramme = function () {

                    $scope.loadingPage = true;

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-sender-message/"+$scope.profileId,

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
                $scope.findMsgProgramme();


                $scope.openFrmMsgProgramme = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/e-module/assurance/message/message-form-details.html',
                        controller: 'InstanceMsgProgramme',
                        resolve: {
                            item: function () {
                                return item || null;
                            }
                        }
                    });

                    modalInstance.result.then(function (result) {
                        console.log('result: ' + item);
                    }, function () {
                        $scope.findMsgProgramme();
                    });
                }

            }
            else{
                $state.go("404");
            }
        })


        .controller('MessageTraiteController', function($rootScope, $scope, $location, $http, alertify, $state,
                                                           $timeout, $cookies, ROOT_URL, $log, $modal, DTOptionsBuilder,
                                                           DTColumnDefBuilder) {

            if($cookies.getObject('uData')) {

                $scope.api = $location.protocol() + "://" + $location.host() + ':' + $location.port() + "" + ROOT_URL.api;

                $scope.jwt_token    = $cookies.getObject('jw_token');

                $scope.profileId    = $cookies.getObject('uData').profile.idDigital

                $scope.loadingPage  = false;

                $scope.datas = {};

                $scope.dtOptions = DTOptionsBuilder
                    .newOptions()
                    .withBootstrap("responsive",!0)
                    .withOption('order', [[0, 'asc']])
                    .withOption('lengthMenu', [10, 50, 150, 250, 300])
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



                $scope.findMsgTraite = function () {

                    $scope.loadingPage = true;

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-sender-message/"+$scope.profileId,

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
                $scope.findMsgTraite();


                $scope.openFrmMsgTraite = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/e-module/assurance/message/message-form-details.html',
                        controller: 'InstanceMsgProgramme',
                        resolve: {
                            item: function () {
                                return item || null;
                            }
                        }
                    });

                    modalInstance.result.then(function (result) {
                        console.log('result: ' + item);
                    }, function () {
                        $scope.findMsgTraite();
                    });
                }

            }
            else{
                $state.go("404");
            }
        })


        .controller('InstanceMsgProgramme', function($scope, $modalInstance, item, $http, alertify, $location,
                                                         ROOT_URL, $state, $cookies) {

            console.clear();

            if($cookies.getObject('uData')){

                $scope.items 		        = {};

                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };


                if (item !== null && item !=='') {

                    $scope.titleFrm          = "Informations sur le message";

                    $scope.items             = item;
                }


            }
            else{
                $state.go("404");
            }
        })
})();