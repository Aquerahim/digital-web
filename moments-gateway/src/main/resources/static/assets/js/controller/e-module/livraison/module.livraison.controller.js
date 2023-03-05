(function() {

    'use strict';

    DiGital

        .controller('MesClientsController', function($rootScope, $scope, $location, $http, alertify, $state,
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
                    DTColumnDefBuilder.newColumnDef(7).notSortable()
                ];


                $scope.findall = function () {

                    $scope.loadingPage = true;

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-clients-module-livraison-by-profile/"+$cookies.getObject('uData').profile.idDigital,

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


                $scope.openFrmMesClients = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/e-module/livraison/client/frm-client.html',

                        controller: 'InstanceMesClients',

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


                $scope.actionOneItemMesClients = function (id, item, action) {

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
            else
                $state.go("404");
        })


        .controller('InstanceMesClients', function($scope, $modalInstance, item, $http, alertify, $location,
                                                 ROOT_URL, $state, $cookies, $log) {

            if($cookies.getObject('uData')){

                $scope.api              = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token        = $cookies.getObject('jw_token');
                $scope.style 		    = "btn-primary";
                $scope.items 		    = {};
                $scope.nomBtn 		    = "Enregistrer";
                $scope.titleFrm         = "Ajouter une nouveau client ou partenaire";
                $scope.url              = "create-new-partaires-d-affaire";
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


                $scope.typeClientList = [
                    {
                        key : "PHYSIQUE",
                        value : "PERSONNE PHYSIQUE"
                    },
                    {
                        key : "ONG",
                        value : "ASSOCIATION CARAITATIVE"
                    },
                    {
                        key : "ENTREPRISE",
                        value : "PERSONNE MORALE"
                    }
                ];


                $scope.qtionNotifsList = [
                    {
                        key : 1,
                        value : "OUI"
                    },
                    {
                        key : 2,
                        value : "NON"
                    }
                ];


                $scope.profileTypeList = [
                    { value : "PARTENAIRE", key: "PARTENAIRE" },
                    { value : "RESPONSABLE STRUCTURE PARTENAIRE", key: "RESPO_PART"  }
                ];


                $scope.userNameGet = function (typeProfile) {

                    var result = '';

                    var chars2 = '9874563210';

                    if (typeProfile) {

                        $scope.timeoutPwd = "120 jours soit 4 Mois";

                        for (var j = 4; j > 0; --j) result += chars2[Math.floor(Math.random() * chars2.length)];

                        switch (typeProfile) {
                            case "PARTENAIRE":
                                $scope.items.username = $scope.abbrev + "-" + result + "P";
                                break;
                            case "RESPO_PART":
                                $scope.items.username = $scope.abbrev + "-" + result + "P7";
                                break;
                        }
                        // $log.info($scope.items.username);
                    }
                };


                $scope.checkField = function (obj) {

                    $scope.actived = obj ? parseInt(obj) !== 1 : true;
                };


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


                $scope.findTypeZone = function () {

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api + '/find-all-type-zone',

                        method: "GET",

                        async: true,

                        cache: false,

                        headers: {

                            'Content-Type': 'application/json',

                            'Authorization': 'Bearer ' + $scope.jwt_token,

                            'Accept': 'application/json',
                        }

                    }).then(function (data) {
                        $scope.typeZoneList = data.data;
                    });
                }();


                $scope.loadSelecBox = function (data, position) {

                    if(position === "T"){

                        if(data){

                            $http({

                                dataType: 'jsonp',

                                url: $scope.api + '/find-all-zone-couverture-by-type/'+data.id,

                                method: "GET",

                                async: true,

                                cache: false,

                                headers: {

                                    'Content-Type': 'application/json',

                                    'Authorization': 'Bearer ' + $scope.jwt_token,

                                    'Accept': 'application/json',
                                }

                            }).then(function (data) {
                                $scope.zoneCouvertureList = data.data;
                            });
                        }
                    }

                };


                $scope.activiteListe = [
                    {index: 1, label: 'AGROALIMENTAIRE'},
                    {index: 2, label: 'COMMERCE'},
                    {index: 3, label: 'NÉGOCE'},
                    {index: 4, label: 'DISTRIBUTION'},
                    {index: 5, label: 'TRANSPORTS'},
                    {index: 6, label: 'LOGISTIQUE'},
                    {index: 7, label: 'SERVICES AUX ENTREPRISES'},
                    {index: 8, label: 'TEXTILE'},
                    {index: 9, label: 'HABILLEMENT'},
                    {index: 10, label: 'CHAUSSURE'},
                    {index: 11, label: 'MÉTALLURGIE'},
                    {index: 12, label: 'TRAVAIL DU MÉTAL'},
                    {index: 13, label: 'INFORMATIQUE'},
                    {index: 14, label: 'LIVRAISON ET COURSES'},
                    {index: 15, label: 'LIVRAISON'},
                    {index: 16, label: 'COURSES'},
                    {index: 17, label: 'RESTAURATION'},
                    {index: 18, label: 'TÉLÉCOMS'},
                    {index: 19, label: 'MACHINES ET ÉQUIPEMENTS'},
                    {index: 20, label: 'AUTOMOBILE'},
                    {index: 21, label: 'ÉTUDES ET CONSEILS'},
                    {index: 22, label: 'INDUSTRIE PHARMACEUTIQUE'},
                    {index: 23, label: 'ÉDITION'},
                    {index: 24, label: 'COMMUNICATION'},
                    {index: 25, label: 'MULTIMÉDIA'},
                    {index: 26, label: 'ÉLECTRONIQUE'},
                    {index: 27, label: 'ÉLECTRICITÉ'},
                    {index: 28, label: 'CHIMIE'},
                    {index: 29, label: 'PARACHIMIE'},
                    {index: 30, label: 'BTP'},
                    {index: 31, label: 'MATÉRIAUX DE CONSTRUCTION'},
                    {index: 32, label: 'BOIS'},
                    {index: 33, label: 'PAPIER'},
                    {index: 34, label: 'CARTON'},
                    {index: 35, label: 'IMPRIMERIE'},
                    {index: 36, label: 'BANQUE'},
                    {index: 37, label: 'ASSURANCE'},
                    {index: 38, label: 'ANIMALERIE'},
                    {index: 39, label: 'AUTRES TYPES'},
                    {index: 40, label: 'E-COMMERCE'}
                ];
                $scope.text2 = '';
                $scope.minlength = 1;


                if (item !== null && item !=='') {

                    $scope.titleFrm = "Modifier informations sur un type de zone";

                    $scope.url = 'update-mes-clients';

                    $scope.style = "btn-success";

                    $scope.nomBtn = "Appliquer"

                    $scope.items = item;

                    $scope.items.typeZoneCouverture = item.zoneCouverture.typeZoneCouverture;

                    $scope.modif = true;

                    $scope.zoneCouvertureList = function () {

                        $http({

                            dataType: 'jsonp',

                            url: $scope.api + '/find-all-zone-couverture-by-type/'+item.zoneCouverture.typeZoneCouverture.id,

                            method: "GET",

                            async: true,

                            cache: false,

                            headers: {

                                'Content-Type': 'application/json',

                                'Authorization': 'Bearer ' + $scope.jwt_token,

                                'Accept': 'application/json',
                            }

                        }).then(function (data) {
                            $scope.zoneCouvertureList = data.data;
                        });
                    }();
                }


                $scope.saveClient = function () {

                    $scope.loadingPage = true;

                    var route = $scope.api + '/' + $scope.url;

                    var method = "POST";

                    if (item === null) {

                        $scope.items.notifAnniv     = $scope.items.notifAnniv === "1";

                        $scope.items.connected      = $scope.items.connected === "1";

                        $scope.items.activite       = $cookies.getObject('uChoice');

                        $scope.items.entreprise     = $scope.items.gare.compagnie;
                    }

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


        .controller('TypeZoneController', function ($rootScope, $scope, $location, $http, alertify, $state,
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

                        url: $scope.api+"/find-all-type-zone",

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


                $scope.openFrmTypeZone = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/e-module/livraison/type-zone/frm-type-zone.html',
                        controller: 'InstanceTypeZone',
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


                $scope.actionOneItemTypeZone = function (id, item, action) {

                    if(action === 1){

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir supprimer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/delete-item-type-zone",

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

                                    url: $scope.api+"/active-item-type-zone",

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


        .controller('InstanceTypeZone', function($scope, $modalInstance, item, $http, alertify, $location,
                                                     ROOT_URL, $state, $cookies, $log) {

            if($cookies.getObject('uData')){

                $scope.api              = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token        = $cookies.getObject('jw_token');

                $scope.api 			= $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.style 		= "btn-primary";
                $scope.items 		= {};
                $scope.nomBtn 		= "Enregistrer";
                $scope.titleFrm     = "Ajouter une nouveau type de zone";
                $scope.url          = "create-new-type-zone";

                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };


                if (item !== null && item !=='') {

                    $scope.titleFrm = "Modifier informations sur un type de zone";

                    $scope.url = 'update-type-zone';

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


        .controller('TypeColisController', function ($rootScope, $scope, $location, $http, alertify, $state,
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

                        url: $scope.api+"/find-all-type-colis",

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


                $scope.openFrmTypeColis = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/e-module/livraison/type-colis/frm-type-colis.html',
                        controller: 'InstanceTypeColis',
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


                $scope.actionOneItemTypeColis = function (id, item, action) {

                    if(action === 1){

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir supprimer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/delete-item-type-colis",

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

                                    url: $scope.api+"/active-item-type-colis",

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


        .controller('InstanceTypeColis', function($scope, $modalInstance, item, $http, alertify, $location,
                                                  ROOT_URL, $state, $cookies, $log) {

            if($cookies.getObject('uData')){

                $scope.api              = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token        = $cookies.getObject('jw_token');
                $scope.style 		= "btn-primary";
                $scope.items 		= {};
                $scope.nomBtn 		= "Enregistrer";
                $scope.titleFrm     = "Ajouter une nouvelle nature de colis";
                $scope.url          = "create-new-type-colis";

                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };


                if (item !== null && item !=='') {

                    $scope.titleFrm = "Modifier informations sur une nature de colis";

                    $scope.url = 'update-type-colis';

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


        .controller('ZoneCouvertureController', function ($rootScope, $scope, $location, $http, alertify, $state,
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
                    .withOption('order', [[2, 'asc']])
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
                    DTColumnDefBuilder.newColumnDef(8).notSortable()
                ];



                $scope.findall = function () {

                    $scope.loadingPage = true;

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-zone-couverture-disponible",

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


                $scope.openFrmZoneCouverture = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/e-module/livraison/zone-couverture/frm-zone-couverture.html',
                        controller: 'InstanceZoneCouverture',
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

                $scope.actionOneItemZoneCouverture = function (id, item, action) {

                    if(action === 1){

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir supprimer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/delete-item-zone-couverture",

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

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir activer l'enregistrement selectionn&eacute; ? ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/active-item-zone-couverture",

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


        .controller('InstanceZoneCouverture', function($scope, $modalInstance, item, $http, alertify, $location,
                                                     ROOT_URL, $state, $cookies, $log) {

            if($cookies.getObject('uData')){

                $scope.api              = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token        = $cookies.getObject('jw_token');

                $scope.api 			= $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.style 		= "btn-primary";
                $scope.items 		= {};
                $scope.nomBtn 		= "Enregistrer";
                $scope.titleFrm     = "Ajouter un nouveau pays autorisé";
                $scope.url          = "create-new-zone-couverture";

                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };


                $scope.findTypeZone = function () {

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api + '/find-all-type-zone',

                        method: "GET",

                        async: true,

                        cache: false,

                        headers: {

                            'Content-Type': 'application/json',

                            'Authorization': 'Bearer ' + $scope.jwt_token,

                            'Accept': 'application/json',
                        }

                    }).then(function (data) {
                        $scope.findTypeZone = data.data;
                    });
                }();


                if (item !== null && item !=='') {

                    $scope.titleFrm = "Modifier informations sur un type de contrat";

                    $scope.url = 'update-zone-couverture';

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


        .controller('MesLivreursController', function ($rootScope, $scope, $location, $http, alertify, $state,
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
                    DTColumnDefBuilder.newColumnDef(5).notSortable()
                ];



                $scope.findall = function () {

                    $scope.loadingPage = true;

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-livreur-module-livraison-by-profile/"+$cookies.getObject('uData').profile.idDigital,

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


                $scope.openFrmLivreur = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/e-module/livraison/livreur/frm-livreur.html',
                        controller: 'InstanceLivreur',
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


                $scope.actionOneItemLivreur = function (id, item, action) {

                    if(action === 1){

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir supprimer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/delete-item-livreur",

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

                                    url: $scope.api+"/active-item-livreur",

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


        .controller('InstanceLivreur', function($scope, $modalInstance, item, $http, alertify, $location,
                                                       ROOT_URL, $state, $cookies, $log) {

            if($cookies.getObject('uData')){

                $cookies.remove('uChoice');
                $cookies.remove('uClient');
                $cookies.remove('uZoneLivraison');
                $cookies.remove('uLivreur');

                $scope.api              = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token        = $cookies.getObject('jw_token');
                $scope.style 		    = "btn-primary";
                $scope.items 		    = {};
                $scope.nomBtn 		    = "Enregistrer";
                $scope.titleFrm         = "Ajouter un nouveau livreur";
                $scope.url              = "create-new-livreur";
                $scope.actived          = true;
                $scope.activedAssurEngin= true;
                $scope.checkTypeEngin   = true;
                $scope.checkTaux        = true;
                $scope.items.profile    = $cookies.getObject('uData').profile;
                $scope.items.entreprise = $cookies.getObject('uData').profile.gareRoutiere.compagnie;
                $scope.abbrev           = $cookies.getObject('uData').profile.gareRoutiere.compagnie.abbrev;
                $scope.modif            = false;
                $scope.items.tauxComm   = 0;

                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };


                if (item !== null && item !=='') {

                    $scope.titleFrm = "Modifier informations sur un livreur";

                    $scope.url = 'update-livreur';

                    $scope.style = "btn-success";

                    $scope.nomBtn = "Appliquer"

                    $scope.items = item;

                    $scope.modif = true;
                }


                $scope.qtionNotifsList = [
                    {
                        key : 1,
                        value : "OUI"
                    },
                    {
                        key : 2,
                        value : "NON"
                    }
                ];


                $scope.profileTypeList = [
                    { value : "LIVREUR OU COURSIER", key: "LIVREUR" }
                ];


                $scope.userNameGet = function (typeProfile) {

                    var result = '';

                    var chars2 = '9874563210';

                    if (typeProfile) {

                        for (var j = 4; j > 0; --j) result += chars2[Math.floor(Math.random() * chars2.length)];

                        switch (typeProfile) {
                            case "LIVREUR":
                                $scope.items.username = $scope.abbrev + "-" + result + "L";
                            break;
                        }
                        //$log.info($scope.items.username);
                    }
                };


                $scope.checkField = function (obj) {

                    $scope.actived = obj ? parseInt(obj) !== 1 : true;
                };


                $scope.checkAssureEnginField = function (obj) {

                    $scope.activedAssurEngin = obj ? obj === "NON" : false;
                };


                $scope.checkTypeEnginField = function (obj) {

                    $scope.checkTypeEngin = obj.id ? parseInt(obj.id) === 4 : false;
                };


                $scope.checktauxCommissionField = function (obj) {

                    $scope.checkTaux = obj.id ? parseInt(obj.id) === 4 : false;
                };


                $scope.typeContratList = function () {

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api + '/find-all-type-contrat-module-livraison',

                        method: "GET",

                        async: true,

                        cache: false,

                        headers: {

                            'Content-Type': 'application/json',

                            'Authorization': 'Bearer ' + $scope.jwt_token,

                            'Accept': 'application/json',
                        }

                    }).then(function (data) {
                        $scope.typeContratList = data.data;
                    });
                }();


                $scope.typeEnginsList = function () {

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api + '/find-all-type-engins',

                        method: "GET",

                        async: true,

                        cache: false,

                        headers: {

                            'Content-Type': 'application/json',

                            'Authorization': 'Bearer ' + $scope.jwt_token,

                            'Accept': 'application/json',
                        }

                    }).then(function (data) {
                        $scope.typeEnginsList = data.data;
                    });
                }();


                $scope.qtionNotifst = [
                    {
                        value : "OUI"
                    },
                    {
                        value : "NON"
                    }
                ];


                $scope.exCompagnieList = [
                    {
                        value : "ACTIVA ASSURANCES COTE D'IVOIRE",
                    },
                    {
                        value : "ALLIANZ COTE D'IVOIRE ASSURANCES"
                    },
                    {
                        value : "AMSA ASSURANCES COTE D'IVOIRE",
                    },
                    {
                        value : "ASSURANCES COMAR COTE D'IVOIRE"
                    },
                    {
                        value : "ATLANTA ASSURANCES COTE D'IVOIRE",
                    },
                    {
                        value : "ATLANTIQUE ASSURANCES COTE D'IVOIRE"
                    },
                    {
                        value : "ATLAS ASSURANCES",
                    },
                    {
                        value : "AVENI-RE"
                    },
                    {
                        value : "AXA COTE D'IVOIRE"
                    },
                    {
                        value : "COMAR",
                    },
                    {
                        value : "CONTINENTAL REINSURANCE (CONTINENTAL-RE)",
                    },
                    {
                        value : "FONDS DE GARANTIE AUTOMOBILE (FGA)"
                    },
                    {
                        value : "GNA-CI"
                    },
                    {
                        value : "HANNOVER RE"
                    },
                    {
                        value : "IPS-CNPS",
                    },
                    {
                        value : "KENYA RE COTE D'IVOIRE"
                    },
                    {
                        value : "L'AFRICAINE DES ASSURANCES"
                    },
                    {
                        value : "LA LOYALE ASSURANCES IARD",
                    },
                    {
                        value : "NSIA-CI ASSURANCES"
                    },
                    {
                        value : "PRUDENTIAL BELIFE INSURANCE",
                    },
                    {
                        value : "SALAM ex SAHAM ASSURANCE CI"
                    },
                    {
                        value : "SERENITY SA"
                    },
                    {
                        value : "SMABTP COTE D'IVOIRE SA",
                    },
                    {
                        value : "SAAR - NON VIE CI"
                    },
                    {
                        value : "SOMAVIE"
                    },
                    {
                        value : "SIDAM SA"
                    },
                    {
                        value : "SIDAM",
                    },
                    {
                        value : "SONAM"
                    },
                    {
                        value : "SUNU ASSURANCES IARD"
                    },
                    {
                        value : "WAFA ASSURANCE COTE D'IVOIRE"
                    },
                    {
                        value : "WAICA REINSURANCE CORPORATION PLC",
                    }
                ];


                $scope.saveLivreur = function () {

                    $scope.loadingPage = true;

                    if (item == null) {

                        $scope.items.notifAnniv     = $scope.items.notifAnniv === "1";
                    }


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


        .controller('AutoCompletController', function($scope) {

            $scope.filtered = [];
            $scope.isVisibles = {
                suggestions: false
            };

            $scope.filterItems = function () {
                if($scope.minlength <= $scope.enteredtext3.length) {
                    $scope.filtered = querySearch($scope.enteredtext3);
                    $scope.isVisibles.suggestions = $scope.filtered.length > 0 ? true : false;
                }
                else {
                    $scope.isVisibles.suggestions = false;
                }
            };


            /**
             * Takes one based index to save selected choice object
             */
            $scope.selectedItem = function (index) {
                $scope.selected3 = $scope.item[index - 1];
                $scope.enteredtext3 = $scope.selected3.label;
                $scope.isVisibles.suggestions = false;
            };

            /**
             * Search for states... use $timeout to simulate
             * remote dataservice call.
             */
            function querySearch (query) {
                // returns list of filtered items
                return  query ? $scope.item.filter( createFilterFor(query) ) : [];
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



        .controller('LivraisonsMultiplesController', function ($rootScope, $scope, $location, $http, alertify, $state,
                                                       $timeout, $cookies, ROOT_URL, $log) {

            console.clear();

            $log.info("================= [vLivraisonsMultiplesControllerv] =================")

            if($cookies.getObject('uData')){

                $scope.items                = {};
                $scope.itemix               = {};
                $scope.api                  = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token            = $cookies.getObject('jw_token');

                $scope.url                  = "create-new-bon-de-livraison";
                $scope.items.profile        = $cookies.getObject('uData').profile;
                $scope.items.entreprise     = $cookies.getObject('uData').profile.gareRoutiere.compagnie;
                $scope.abbrev               = $cookies.getObject('uData').profile.gareRoutiere.compagnie.abbrev;


                $scope.findNatureColis = function () {

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api + '/find-all-type-colis',

                        method: "GET",

                        async: true,

                        cache: false,

                        headers: {

                            'Content-Type': 'application/json',

                            'Authorization': 'Bearer ' + $scope.jwt_token,

                            'Accept': 'application/json',
                        }

                    }).then(function (data) {
                        $scope.natureColisList = data.data;
                    });
                }();


                $scope.findTypeZone = function () {

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api + '/find-all-type-zone',

                        method: "GET",

                        async: true,

                        cache: false,

                        headers: {

                            'Content-Type': 'application/json',

                            'Authorization': 'Bearer ' + $scope.jwt_token,

                            'Accept': 'application/json',
                        }

                    }).then(function (data) {
                        $scope.typeZoneList = data.data;
                    });
                }();


                $scope.qtionNotifst = [
                    {
                        value : "OUI"
                    },
                    {
                        value : "NON"
                    }
                ];


                $scope.findAllLivreurs = function () {

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-livreur-module-livraison-disponible/"+$cookies.getObject('uData').profile.idDigital,

                        method: "GET",

                        async: true,

                        cache: false,

                        headers: {

                            'Content-Type': 'application/json',

                            'Authorization': 'Bearer ' + $scope.jwt_token,

                            'Accept': 'application/json',
                        }

                    }).then(function (data) {

                        $scope.livreurDisponibleLits = data.data;
                    });
                }();


                $scope.zoneRecupLits = [];
                $scope.zoneRecup = {};
                $scope.loadSelecBoxMLivr = function (data, position) {

                    if(position === "T"){

                        if(data){

                            $http({

                                dataType: 'jsonp',

                                url: $scope.api + '/find-all-zone-couverture-by-type/'+data.id,

                                method: "GET",

                                async: true,

                                cache: false,

                                headers: {

                                    'Content-Type': 'application/json',

                                    'Authorization': 'Bearer ' + $scope.jwt_token,

                                    'Accept': 'application/json',
                                }

                            }).then(function (data) {
                                $scope.zoneRecuperationList = data.data;
                            });
                        }
                    }

                    if(position === "TX"){

                        if(data){

                            $http({

                                dataType: 'jsonp',

                                url: $scope.api + '/find-all-zone-couverture-by-type/'+data.id,

                                method: "GET",

                                async: true,

                                cache: false,

                                headers: {

                                    'Content-Type': 'application/json',

                                    'Authorization': 'Bearer ' + $scope.jwt_token,

                                    'Accept': 'application/json',
                                }

                            }).then(function (data) {
                                $scope.zoneLivraisonList = data.data;
                            });
                        }
                    }
                };

            }
            else{
                $state.go("404");
            }
        })


        .controller('MesLivraisonsController', function ($rootScope, $scope, $location, $http, alertify, $state,
                                                       $timeout, $cookies, ROOT_URL, $log, $modal, DTOptionsBuilder,
                                                       DTColumnDefBuilder) {
            if($cookies.getObject('uData')){

                $scope.api              = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token        = $cookies.getObject('jw_token');
                $scope.loadingPage      = false;
                $scope.datas            = {};


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

                        url: $scope.api+"/find-all-livraison-by-profile/"+$cookies.getObject('uData').profile.idDigital,

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



                $scope.livraionSucces = function () {

                    $scope.loadingPage = true;

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-livraison-succes-by-profile/"+$cookies.getObject('uData').profile.idDigital,

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
                                $scope.datasSuccess = data.data;
                                //$scope.existData = data.data.length > 0;
                            },
                            function(data, status, xhrStatus){
                                $scope.loadingPage = false;
                            });
                }();



                $scope.livraionPending = function () {

                    $scope.loadingPage = true;

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-livraison-pending-by-profile/"+$cookies.getObject('uData').profile.idDigital,

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
                                $scope.datasPending = data.data;
                                //$scope.existData = data.data.length > 0;
                            },
                            function(data, status, xhrStatus){
                                $scope.loadingPage = false;
                            });
                }();


                $scope.openFrmBonLivraison = function(event, item) {

                    if(item.statutLivraison === 'EN_COURS' && item != null){

                        var modalInstance = $modal.open({

                            templateUrl: ROOT_URL.absolute + '/e-module/livraison/m-livraison/frm-livraison.html',
                            controller: 'InstanceBonLivraison',
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
                    else{

                        var modalInstance = $modal.open({

                            templateUrl: ROOT_URL.absolute + '/e-module/livraison/m-livraison/frm-livraison.html',
                            controller: 'InstanceBonLivraison',
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


                /*
                $scope.openFrmBonLivraison = function(event, item) {

                    if(item.statutLivraison === 'EN_COURS'){

                        var modalInstance = $modal.open({

                            templateUrl: ROOT_URL.absolute + '/e-module/livraison/m-livraison/frm-livraison.html',
                            controller: 'InstanceBonLivraison',
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
                    else {

                        alertify.alert("<strong>Di-Gital web</strong> : Les modifications sur cette livraison n'est plus possible car elle a chang&eacute; d'&eacute;tat.");
                    }
                }
                 */


                $scope.actionOneItemBonLivraison = function (id, item, action) {

                    if(action === 1){

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir supprimer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/delete-item-livreur",

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

                                    url: $scope.api+"/active-item-livreur",

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


                $scope.openFrmDetailsLivraison = function(event, item) {

                    var modalInstance = $modal.open({

                        templateUrl: ROOT_URL.absolute + '/e-module/livraison/m-livraison/details-frm-livraison.html',
                        controller: 'InstanceDetailsBonLivraison',
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


        .controller('InstanceBonLivraison', function($scope, $modalInstance, item, $http, alertify, $location,
                                                     ROOT_URL, $state, $cookies, $log) {

            if($cookies.getObject('uData')){

                $scope.api                  = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token            = $cookies.getObject('jw_token');
                $scope.style 		        = "btn-primary";
                $scope.items 		        = {};
                $scope.nomBtn 		        = "Enregistrer";
                $scope.titleFrm             = "Ajouter un nouveau bon de livraison";
                $scope.url                  = "create-new-bon-de-livraison";
                $scope.items.profile        = $cookies.getObject('uData').profile;
                $scope.items.entreprise     = $cookies.getObject('uData').profile.gareRoutiere.compagnie;
                $scope.abbrev               = $cookies.getObject('uData').profile.gareRoutiere.compagnie.abbrev;
                $scope.modif                = false;
                $scope.items.montantColis   = 0;
                $scope.items.prixLivraison  = 0;
                $scope.items.qte            = 0;


                $scope.findNatureColis = function () {

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api + '/find-all-type-colis',

                        method: "GET",

                        async: true,

                        cache: false,

                        headers: {

                            'Content-Type': 'application/json',

                            'Authorization': 'Bearer ' + $scope.jwt_token,

                            'Accept': 'application/json',
                        }

                    }).then(function (data) {
                        $scope.natureColisList = data.data;
                    });
                }();


                $scope.findTypeZone = function () {

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api + '/find-all-type-zone',

                        method: "GET",

                        async: true,

                        cache: false,

                        headers: {

                            'Content-Type': 'application/json',

                            'Authorization': 'Bearer ' + $scope.jwt_token,

                            'Accept': 'application/json',
                        }

                    }).then(function (data) {
                        $scope.typeZoneList = data.data;
                    });
                }();


                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };

                //$scope.clientLits = [];
                $scope.clients = {};
                $scope.findAllPorteFeuille = function () {

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-clients-module-livraison-by-profile/"+$cookies.getObject('uData').profile.idDigital,

                        method: "GET",

                        async: true,

                        cache: false,

                        headers: {

                            'Content-Type': 'application/json',

                            'Authorization': 'Bearer ' + $scope.jwt_token,

                            'Accept': 'application/json',
                        }

                    }).then(function (data) {

                        /*angular.forEach(data.data, function (value, key) {

                            $scope.clients.index = value.id;

                            $scope.clients.label = value.nomComplet;

                            $scope.clientLits.push($scope.clients);

                            $scope.clients = {};
                        });*/

                        $scope.clientsList = data.data;

                        //$log.info($scope.clientLits)
                    });
                }();

                $scope.zoneLivraisonLits = [];
                $scope.zoneLivraison = {};
                $scope.findAllZone = function () {

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-zone-couverture-disponible",

                        method: "GET",

                        async: true,

                        cache: false,

                        headers: {

                            'Content-Type': 'application/json',

                            'Authorization': 'Bearer ' + $scope.jwt_token,

                            'Accept': 'application/json',
                        }

                    }).then(function (data) {

                        angular.forEach(data.data, function (value, key) {

                            $scope.zoneLivraison.index = value.id;

                            $scope.zoneLivraison.label = value.zoneCouverture;

                            $scope.zoneLivraisonLits.push($scope.zoneLivraison);

                            $scope.zoneLivraison = {};
                        });
                    });
                }();


                $scope.generateRef = function () {

                    var result = '';

                    var result1 = '';

                    var chars = '9874563210';

                    var chars1 = 'AZERTYUIOPMLKJHGFDSQWXCVBN';

                    for (var j = 6; j > 0; --j) result += chars[Math.floor(Math.random() * chars.length)];

                    for (var j = 3; j > 0; --j) result1 += chars1[Math.floor(Math.random() * chars1.length)];

                    $scope.items.reference = "RF-"+result + "-" + result1;

                }();


                $scope.zoneRecupLits = [];
                $scope.zoneRecup = {};
                $scope.loadSelecBoxMLivr = function (data, position) {

                    if(position === "T"){

                        if(data){

                            $http({

                                dataType: 'jsonp',

                                url: $scope.api + '/find-all-zone-couverture-by-type/'+data.id,

                                method: "GET",

                                async: true,

                                cache: false,

                                headers: {

                                    'Content-Type': 'application/json',

                                    'Authorization': 'Bearer ' + $scope.jwt_token,

                                    'Accept': 'application/json',
                                }

                            }).then(function (data) {
                                $scope.zoneRecuperationList = data.data;
                            });
                        }
                    }

                    if(position === "TX"){

                        if(data){

                            $http({

                                dataType: 'jsonp',

                                url: $scope.api + '/find-all-zone-couverture-by-type/'+data.id,

                                method: "GET",

                                async: true,

                                cache: false,

                                headers: {

                                    'Content-Type': 'application/json',

                                    'Authorization': 'Bearer ' + $scope.jwt_token,

                                    'Accept': 'application/json',
                                }

                            }).then(function (data) {
                                $scope.zoneLivraisonList = data.data;
                            });
                        }
                    }
                };



                $scope.qtionNotifst = [
                    {
                        value : "OUI"
                    },
                    {
                        value : "NON"
                    }
                ];


                $scope.findAllLivreurs = function () {

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-livreur-module-livraison-disponible/"+$cookies.getObject('uData').profile.idDigital,

                        method: "GET",

                        async: true,

                        cache: false,

                        headers: {

                            'Content-Type': 'application/json',

                            'Authorization': 'Bearer ' + $scope.jwt_token,

                            'Accept': 'application/json',
                        }

                    }).then(function (data) {

                        $scope.livreurLits = data.data;
                    });
                }();


                if (item !== null && item !=='') {

                    $scope.titleFrm = "Modifier informations du bon de livraison au n° d'ordre "+ item.ordre;

                    $scope.url = 'update-bon-de-livraison';

                    $scope.style = "btn-success";

                    $scope.nomBtn = "Appliquer"

                    $scope.items = item;

                    $scope.modif = true;

                    $scope.findAllZoneRecup = function () {

                        $http({

                            dataType: 'jsonp',

                            url: $scope.api + '/find-all-zone-couverture-by-type/'+item.zoneRecuperation.typeZoneCouverture.id,

                            method: "GET",

                            async: true,

                            cache: false,

                            headers: {

                                'Content-Type': 'application/json',

                                'Authorization': 'Bearer ' + $scope.jwt_token,

                                'Accept': 'application/json',
                            }

                        }).then(function (data) {

                            $scope.findAllZoneRecup = data.data;
                        });
                    }();

                    $scope.findAllZoneLivr = function () {

                        $http({

                            dataType: 'jsonp',

                            url: $scope.api + '/find-all-zone-couverture-by-type/'+item.zoneLivraison.typeZoneCouverture.id,

                            method: "GET",

                            async: true,

                            cache: false,

                            headers: {

                                'Content-Type': 'application/json',

                                'Authorization': 'Bearer ' + $scope.jwt_token,

                                'Accept': 'application/json',
                            }

                        }).then(function (data) {

                            $scope.findAllZoneLivr = data.data;
                        });
                    }();
                }



                $scope.saveBonLivraison = function () {

                    $scope.loadingPage = true;

                    var route = $scope.api + '/' + $scope.url;

                    var method = "POST";

                    /*if (item === null) {

                        $scope.items.clientId  = $cookies.getObject('uClient');
                    }*/

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

                $scope.text3 = '';
                $scope.text2 = '';
                $scope.minlength = 1;
                $scope.selectedA = {};
                $scope.selected2 = {};


                $scope.tab          = 1;
                $scope.setTabs = function(newTab){

                    $scope.tab = newTab;
                };


                $scope.isSets = function(tabNum){

                    return $scope.tab === tabNum;
                };

            }
            else{
                $state.go("404");
            }
        })


        .controller('InstanceDetailsBonLivraison', function($scope, $modalInstance, item, alertify, $state,
                                                            $cookies, $log, $location, $http, ROOT_URL) {

            $scope.titleFrm     = "Détails de la fiche de livraison - à la référence "+item.reference;
            $scope.items        = {};
            $scope.tab          = 1;
            $scope.items        = item;
            $scope.api          = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
            $scope.jwt_token    = $cookies.getObject('jw_token');

            $scope.setTab = function(newTab){

                $scope.tab = newTab;
            };


            $scope.isSet = function(tabNum){

                return $scope.tab === tabNum;
            };


            $scope.fermer = function() {
                $modalInstance.dismiss('cancel');
            };

            $scope.items.livreur.dateFinAssurance = new Date(item.livreur.dateFinAssurance);

            $scope.findAllHistorique = function () {

                $http({

                    dataType: 'jsonp',

                    url: $scope.api+"/find-all-historique-livraison-by-ref/"+item.reference,

                    method: "GET",

                    async: true,

                    cache: false,

                    headers: {

                        'Content-Type': 'application/json',

                        'Authorization': 'Bearer ' + $scope.jwt_token,

                        'Accept': 'application/json',
                    }

                }).then(function (data) {

                    $scope.historiqueList = data.data;

                    //$log.info($scope.historiqueList)
                });
            }();

        })


        .controller('AcheminementController', function ($rootScope, $scope, $location, $http, alertify, $state,
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

                        url: $scope.api+"/find-all-livraison-by-profile-with-notification/"+$cookies.getObject('uData').profile.idDigital,

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



                $scope.notifDestinatairePourLivraison = function (id, item) {

                    alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> Souhaitez-vous notifier le client de l'acheminement de sa commande par le livreur d&eacute;sign&eacute; ?",function(event){

                        if(event) {

                            $scope.loadingPage = true;

                            $http({

                                url: $scope.api+"/achiminement-colis-notification-customer",

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
                                    alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                                }

                            },function(data, status, headers, config){
                                $scope.loadingPage = false;
                                alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.message);
                            });
                        }
                    });
                };


                $scope.notififiactionLivraison = function (id, item) {

                    if(item != null){

                        if(item.statutLivraison === 'LIVRE'){

                            alertify.alert("<strong>Di-Gital web</strong> : Tous les messages nécéssaires ont été déjà envoyé aux différents acteurs ayant interven");
                        }
                        else{

                            var modalInstance = $modal.open({

                                templateUrl: ROOT_URL.absolute + '/e-module/livraison/modal/frm-modal.html',

                                controller: 'InstanceFrmModal',

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
                }

                $scope.nonCaseLivraison = function (id, item) {

                    if(item != null){

                        if(item.statutLivraison === 'EN_COURS'){

                            var modalInstance = $modal.open({

                                templateUrl: ROOT_URL.absolute + '/e-module/livraison/modal/frm-non-case.html',

                                controller: 'InstanceNonCaseModal',

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
                        else{

                            alertify.alert("<strong>Di-Gital web</strong> : Une livraison ayant &eacute;t&eacute; effectu&eacute;e ne peut &ecirc;tre traiter en Non-Case");
                        }
                    }
                }

            }
            else{
                $state.go("404");
            }
        })

        .controller('InstanceNonCaseModal', function($scope, $modalInstance, item, $http, alertify, $location,
                                                 ROOT_URL, $state, $cookies, $log) {

            $scope.api              = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
            $scope.jwt_token        = $cookies.getObject('jw_token');
            $scope.items            = {};
            $scope.tab              = 1;

            $scope.fermer = function() {
                $modalInstance.dismiss('cancel');
            };

            $scope.setTabNonCase = function(newTab){
                $scope.tab = newTab;
            };


            $scope.isSetNonCase = function(tabNum){
                return $scope.tab === tabNum;
            };

            $scope.statutLivraisonList = [
                {
                    key : "ANNULER",
                    value : "LIVRAISON ANNULEE"
                },
                {
                    key : "REFUS",
                    value : "COLIS REFUSE PAR LE CLIENT"
                },
                {
                    key : "NON_CASE",
                    value : "LIVRAISON PAYE MAIS COLIS NON PRIS PAR LE CLIENT"
                }
            ];


            if (item !== null && item !=='') {

                $scope.datelivraison = moment(item.datelivraison, 'YYYY-MM-DD').format('DD-MM-YYYY');

                $scope.items = item;
            }


            $scope.updateStatuLivraison = function () {

                $scope.loadingPage              = true;

                $scope.items.statutRefus        = $scope.statuTraitement.key;

                $scope.items.motifNonLivraison  = $scope.statuTraitement.key === 'ANNULER' ? $scope.items.motifNonLivraison : $scope.statuTraitement.value;

                $http({

                    url: $scope.api + '/non-case-traitement-bon-de-livraison',

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

                    }
                    else if (data.data.information.requestCode === 400) {

                        alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);

                    }
                    else {

                        alertify.alert("<strong>Di-Gital web</strong> : Impossible de traiter votre requ&ecirc;te.");
                    }

                }, function (data, status, headers, config) {
                    $scope.loadingPage = false;

                    alertify.alert("<strong>Di-Gital web</strong> : " + ata.message);
                });
            };

        })


        .controller('InstanceFrmModal', function($scope, $modalInstance, item, $http, alertify, $location,
                                                              ROOT_URL, $state, $cookies, $log) {

            $scope.api              = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
            $scope.jwt_token        = $cookies.getObject('jw_token');
            $scope.jons             = {};
            $scope.jons             = item;


            $scope.fermer = function() {
                $modalInstance.dismiss('cancel');
            };


            $scope.sendMessage = function(typeMessage) {

                if(typeMessage === 'Remerciement'){

                    $scope.jons.action = "Remerciement";

                    $scope.loadingPage = true;

                    $http({

                        url: $scope.api+"/notification-acteur",

                        method: "PUT",

                        data: JSON.stringify($scope.jons),

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

                            $modalInstance.dismiss('cancel');

                        }
                        else {
                            alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                        }

                    },function(data, status, headers, config){

                        $scope.loadingPage = false;

                        alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : Impossible de traiter votre requ&ecirc;te. Si le probl&egrave;me persiste nous vous invitons à contacter l'administrateur.");
                    });
                }
                else{

                    $scope.jons.action = "Livreur-Arrivé";

                    $scope.loadingPage = true;

                    $http({

                        url: $scope.api+"/notification-acteur",

                        method: "PUT",

                        data: JSON.stringify($scope.jons),

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
                        alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> :  Impossible de traiter votre requ&ecirc;te. Si le probl&egrave;me persiste nous vous invitons à contacter l'administrateur.");
                    });
                }
            };

        })


        .controller('NewColisJournalierController', function ($rootScope, $scope, $location, $http, alertify, $state,
                                                        $timeout, $cookies, ROOT_URL, $log) {
            if($cookies.getObject('uData')){

                $scope.items            = {};
                $scope.api              = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token        = $cookies.getObject('jw_token');
                $scope.items.profile    = $cookies.getObject('uData').profile;

                $scope.loadingPage      = false;
                $scope.actived          = true;
                $scope.jxes             = {};
                $scope.nb               = {};
                $scope.nb.colis         = 0;
                $scope.cpte             = 0;

                $scope.goBack = function (route) {
                    $state.go(route);
                }

                //$log.info($scope.items.profile)

                $scope.init = function(){

                    $scope.tabPrice = [];

                    for( var i=1000; i<=10000; i+=500){

                        $scope.tabPrice.push(i);
                    }

                    var result = '';

                    var chars = '98745632100123456789';

                    for (var j = 6; j > 0; --j) result += chars[Math.floor(Math.random() * chars.length)];

                    $scope.items.reference = "CJ-"+result;

                }();

                $scope.typeEnvoiListBc = [
                    {
                        key : "EXPRESS",
                        value : "LIVRAISON EXPRESS"
                    },
                    {
                        key : "NORMAL",
                        value : "LIVRAISON NORMAL"
                    }
                ];

                $scope.getClients = function () {

                    //$log.info($cookies.getObject('uData').profile)

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api + '/get-client-by-profile-liaison/'+$cookies.getObject('uData').profile.idDigital,

                        method: "GET",

                        async: true,

                        cache: false,

                        headers: {

                            'Content-Type': 'application/json',

                            'Authorization': 'Bearer ' + $scope.jwt_token,

                            'Accept': 'application/json',
                        }

                    }).then(function (data) {
                        if(data.data != null){
                            //$log.info(data.data)
                            $scope.partnersName         = data.data.nomComplet;
                            $scope.partnersContact      = data.data.contact;
                            $scope.partnerOwner         = data.data.nomResponsable;
                            $scope.ownerPhone           = data.data.contactResponsable;
                            $scope.activity             = data.data.activite;
                            $scope.ownerMail            = data.data.email;
                            $scope.items.ownerNumber    = data.data.ordre;
                        }
                    });
                }();

                $scope.typeZoneListBc = function () {

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api + '/find-all-type-zone',

                        method: "GET",

                        async: true,

                        cache: false,

                        headers: {

                            'Content-Type': 'application/json',

                            'Authorization': 'Bearer ' + $scope.jwt_token,

                            'Accept': 'application/json',
                        }

                    }).then(function (data) {
                        $scope.typeZoneListBc = data.data;
                    });
                }();


                $scope.zoneRecupLits = [];
                $scope.zoneRecup = {};

                $scope.loadPerimetreCreateFiche = function (data, position) {

                    if(position === 0){

                        if(data){

                            $http({

                                dataType: 'jsonp',

                                url: $scope.api + '/find-all-zone-couverture-by-type/'+data.id,

                                method: "GET",

                                async: true,

                                cache: false,

                                headers: {

                                    'Content-Type': 'application/json',

                                    'Authorization': 'Bearer ' + $scope.jwt_token,

                                    'Accept': 'application/json',
                                }

                            }).then(function (data) {
                                $scope.zoneRecuperationListBc = data.data;
                            });
                        }
                    }

                    if(position === 1){

                        if(data){

                            $http({

                                dataType: 'jsonp',

                                url: $scope.api + '/find-all-zone-couverture-by-type/'+data.id,

                                method: "GET",

                                async: true,

                                cache: false,

                                headers: {

                                    'Content-Type': 'application/json',

                                    'Authorization': 'Bearer ' + $scope.jwt_token,

                                    'Accept': 'application/json',
                                }

                            }).then(function (data) {
                                $scope.zoneRecuperationListBc1 = data.data;
                            });
                        }
                    }
                };


                $scope.natureColisListBc = function () {

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api + '/find-all-type-colis',

                        method: "GET",

                        async: true,

                        cache: false,

                        headers: {

                            'Content-Type': 'application/json',

                            'Authorization': 'Bearer ' + $scope.jwt_token,

                            'Accept': 'application/json',
                        }

                    }).then(function (data) {
                        $scope.natureColisListBc = data.data;
                    });
                }();


                $scope.qtionNotifstBc = [
                    {
                        value : "OUI"
                    },
                    {
                        value : "NON"
                    }
                ];


                //Ajout des colis
                $scope.bonCmde = [];

                $scope.addToList = function () {

                    $scope.cpte ++;

                    $scope.jxes.id = $scope.cpte;

                    $scope.bonCmde.push($scope.jxes);

                    $scope.nb.colis = $scope.bonCmde.length;

                    $scope.jxes = {};
                }


                $scope.retrierToList = function(item){
                    $scope.bonCmde.splice( $scope.bonCmde.indexOf(item), 1);
                }



                $scope.saveBcPartner = function () {

                    $scope.loadingPage  = true;
                    $scope.items.bonDeCommandeList      = $scope.bonCmde;

                    $http({

                        url: $scope.api + '/save-bon-de-commande',

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

                            alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : L'enregistrement du bon de recup&eacute;ration de colis effectu&eacute; avec succ&egrave;s. Des notifications sms et e-mail ont &eacute;t&eacute; envoy&eacute; &agrave; la soci&eacute;t&eacute;. Toute modification est encore possible avant validation de la part de la la soci&eacute;t&eacute; de livraison",function(event){

                                if(event) {

                                    $state.go("mainpage.list-colis-journalier");
                                }
                            });
                        }

                        else if (data.data.information.requestCode === 409) {
                            alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);
                        }

                        else if (data.data.information.requestCode === 400) {
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


        .controller('ListingColisJournalierController', function ($rootScope, $scope, $location, $http, alertify, $state,
                                                        $timeout, $cookies, ROOT_URL, $log, $modal, DTOptionsBuilder,
                                                                  DTColumnDefBuilder, $filter) {
            if($cookies.getObject('uData')){

                $scope.items            = {};
                $scope.printRecu        = {};
                $scope.api              = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token        = $cookies.getObject('jw_token');
                $scope.items.profile    = $cookies.getObject('uData').profile;
                var _this               = this;
                $scope.loadingPage      = false;
                $scope.datas            = {};

                //$log.info($scope.items.profile)

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


                $scope.findall = function () {

                    $scope.loadingPage = true;

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-bon-de-commande-by-profile/"+$cookies.getObject('uData').profile.idDigital,

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


                $scope.actionOneItemFichLivraison = function (id, item, action) {

                    if(action === 1){

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir supprimer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/delete-item-bon-de-commande",

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

                                    url: $scope.api+"/active-item-bon-de-commande",

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



                $scope.impressionFiche = function (data) {

                    //$log.info(data)

                     if(data){

                         $http({

                             dataType: 'jsonp',

                             url: $scope.api+"/find-one-fiche-bon-de-commande-by-ref/"+data.reference,

                             method: "GET",

                             async: true,

                             cache: false,

                             headers: {

                                 'Content-Type': 'application/json',

                                 'Authorization': 'Bearer ' + $scope.jwt_token,

                                 'Accept': 'application/json',
                             }
                         })
                         .then(function(datas){

                             $scope.printRecu.articles                  = datas.data.bonDeCommandeList;

                             $scope.printRecu.logoPhoenix               = ROOT_URL.logoPhoenix;
                             $scope.printRecu.logoEntreprise            = ROOT_URL.pathImage+"/"+data.partenaire.entreprise.logo;
                             $scope.printRecu.arrierePlan               = ROOT_URL.arrierePlan;
                             $scope.printRecu.nomEntreprise             = data.partenaire.entreprise.compagnie;
                             $scope.printRecu.nomPartenaire             = data.partenaire.nomComplet;

                             $scope.printRecu.abbrev                    = data.partenaire.entreprise.abbrev.toUpperCase();
                             $scope.printRecu.compagnie                 = data.partenaire.entreprise.compagnie.toUpperCase();
                             $scope.printRecu.contact                   = data.partenaire.entreprise.contact;
                             $scope.printRecu.contactRespEntrep         = data.partenaire.entreprise.contactResponsable;
                             $scope.printRecu.adresse                   = data.partenaire.entreprise.adresse;
                             $scope.printRecu.rccm                      = data.partenaire.entreprise.rccm;
                             $scope.printRecu.email                     = data.partenaire.entreprise.email.toLowerCase();

                             $scope.printRecu.contactResponsable        = data.partenaire.contactResponsable;
                             $scope.printRecu.activite                  = data.partenaire.activite;
                             $scope.printRecu.ordre                     = data.partenaire.ordre;
                             $scope.printRecu.contactPart               = data.partenaire.contact;
                             $scope.printRecu.nomResponsable            = data.partenaire.nomResponsable;
                             $scope.printRecu.zoneCouvPart              = data.partenaire.zoneCouverture.typeZoneCouverture.typezone+" dans la commune de "+ data.partenaire.zoneCouverture.zoneCouverture + " plus précisement "+ data.partenaire.precisonZone.toUpperCase();

                             $scope.printRecu.reference                 = data.reference;
                             $scope.printRecu.typeLivraison             = data.typeLivraison;
                             $scope.printRecu.datelivraisonSouhaite     = moment(data.datelivraisonSouhaite, 'YYYY-MM-DD').format('DD/MM/YYYY');
                             $scope.printRecu.zoneRecuperation          = data.zoneRecuperation.zoneCouverture;
                             $scope.printRecu.typeZoneRecup             = data.zoneRecuperation.typeZoneCouverture.typezone;
                             $scope.printRecu.precisionZoneRecup        = data.precisionZoneRecup;
                             $scope.printRecu.montantGlobal             = data.montantGlobal;
                             $scope.printRecu.montantLivraisn           = data.montantGlobalLivraison;
                             $scope.printRecu.auteur                    = data.profile.nomPrenoms;
                             $scope.printRecu.etat                      = data.suiviDemande.suivi;

                             //$log.info($scope.printRecu);

                             _this.imprimerDocument("fiche-de-colis-journalier", false, $scope.printRecu, "fiche-de-colis-journalier"+data.ordre+".pdf");

                         });
                     }
                };

                $scope.impressionAllDataToFiche = function (donnes) {

                    if(donnes){

                        $scope.printRecu.logoPhoenix        = ROOT_URL.logoPhoenix;

                        $scope.printRecu.logoEntreprise     = ROOT_URL.pathImage+"/"+$scope.items.profile.gareRoutiere.compagnie.logo;

                        $scope.printRecu.auteur             = $scope.items.profile.nomPrenoms;

                        $scope.printRecu.list               = donnes;

                        _this.imprimerDocument("fiche-all-colis-journalier", false, $scope.printRecu, "fiche-de-colis-all-journalier"+$scope.items.profile.idDigital+".pdf");
                    }
                    else{
                        alertify.alert("<strong>Di-Gital web</strong> : Impression de la liste impossible. Pas de donn&eacute;es disponibles");
                    }

                }



                /**
                 * Ouverture/impression du document au format PDF
                 */
                this.imprimerDocument = function (urlImpression, finParcours, data, fileName){

                    $scope.loadingPage = true;

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

                            $scope.loadingPage = false;

                        }
                        else{

                            var file = new Blob([response.data], {type: 'application/pdf'});

                            var fileURL = window.URL.createObjectURL(file);

                            var win = window.open(fileURL, '_blank');

                            if(win){
                                win.focus();
                            }
                            $scope.loadingPage = false;
                        }

                    }, function errorCallback(response) {
                        alertify.alert("<strong>Di-Gital web</strong> : Service non disponible");
                        $scope.loadingPage = false;
                    });
                }

            }
            else{
                $state.go("404");
            }
        })

        .controller('DetailsColisJournalierController', function ($rootScope, $scope, $location, $http, alertify, $state,
                                                                  $timeout, $cookies, ROOT_URL, $log, $modal, $stateParams,
                                                                  $filter) {

            if($cookies.getObject('uData')) {

                $scope.items            = {};
                $scope.api              = $location.protocol() + "://" + $location.host() + ':' + $location.port() + "" + ROOT_URL.api;
                $scope.jwt_token        = $cookies.getObject('jw_token');
                $scope.items.profile    = $cookies.getObject('uData').profile;
                $scope.reference        = $stateParams.ref;

                $scope.tab = 1;

                $scope.setTabBc = function(newTab){
                    $scope.retrait  = {};
                    $scope.refColis = null;
                    $scope.donnees  = {};
                    $scope.tab = newTab;
                };

                $scope.isSetBc = function(tabNum){
                    return $scope.tab === tabNum;
                };


                $scope.goBack1 = function (route) {
                    $state.go(route);
                }

                if($scope.reference){

                    $scope.findOne = function () {

                        $scope.loadingPage = true;

                        $http({

                            dataType: 'jsonp',

                            url: $scope.api+"/find-one-fiche-bon-de-commande-by-ref/"+$scope.reference,

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

                            if(data.status === 200){

                                $scope.items = data.data;

                                //$log.info($filter('date')(data.data.datelivraisonSouhaite, 'dd/MM/yyyy'))
                                $scope.datelivraisonSouhaite = $filter('date')(data.data.datelivraisonSouhaite, 'dd/MM/yyyy');
                                $scope.items.datelivraisonSouhaite = new Date(data.data.datelivraisonSouhaite);

                                //$scope.items.datelivraisonSouhaite = new Date($filter('date')(data.data.datelivraisonSouhaite, 'dd/MM/yyyy'));
                            }


                            $scope.findAllZoneRecup = function () {

                                    $http({

                                        dataType: 'jsonp',

                                        url: $scope.api + '/find-all-zone-couverture-by-type/'+$scope.items.zoneRecuperation.typeZoneCouverture.id,

                                        method: "GET",

                                        async: true,

                                        cache: false,

                                        headers: {

                                            'Content-Type': 'application/json',

                                            'Authorization': 'Bearer ' + $scope.jwt_token,

                                            'Accept': 'application/json',
                                        }

                                    }).then(function (data) {

                                        $scope.zoneRecuperationListBc = data.data;
                                    });
                                }();
                        },
                        function(data, status, xhrStatus){
                            $scope.loadingPage = false;
                        });
                    };

                    $scope.findOne();


                    $scope.typeZoneListBc = function () {

                        $http({

                            dataType: 'jsonp',

                            url: $scope.api + '/find-all-type-zone',

                            method: "GET",

                            async: true,

                            cache: false,

                            headers: {

                                'Content-Type': 'application/json',

                                'Authorization': 'Bearer ' + $scope.jwt_token,

                                'Accept': 'application/json',
                            }

                        }).then(function (data) {
                            $scope.typeZoneListBc = data.data;
                        });
                    }();


                    $scope.loadPerimetre = function (data, position) {

                        if(position === 0){

                            if(data){

                                $http({

                                    dataType: 'jsonp',

                                    url: $scope.api + '/find-all-zone-couverture-by-type/'+data.id,

                                    method: "GET",

                                    async: true,

                                    cache: false,

                                    headers: {

                                        'Content-Type': 'application/json',

                                        'Authorization': 'Bearer ' + $scope.jwt_token,

                                        'Accept': 'application/json',
                                    }

                                }).then(function (data) {
                                    $scope.zoneRecuperationListBc = data.data;
                                });
                            }
                        }

                        if(position === 1){

                            if(data){

                                $http({

                                    dataType: 'jsonp',

                                    url: $scope.api + '/find-all-zone-couverture-by-type/'+data.id,

                                    method: "GET",

                                    async: true,

                                    cache: false,

                                    headers: {

                                        'Content-Type': 'application/json',

                                        'Authorization': 'Bearer ' + $scope.jwt_token,

                                        'Accept': 'application/json',
                                    }

                                }).then(function (data) {
                                    $scope.zoneRecuperationListBc1 = data.data;
                                });
                            }
                        }
                    };


                    $scope.typeEnvoiListBc = [
                        {
                            key : "EXPRESS",
                            value : "LIVRAISON EXPRESS"
                        },
                        {
                            key : "NORMAL",
                            value : "LIVRAISON NORMAL"
                        }
                    ];


                    $scope.natureColisListBc = function () {

                        $http({

                            dataType: 'jsonp',

                            url: $scope.api + '/find-all-type-colis',

                            method: "GET",

                            async: true,

                            cache: false,

                            headers: {

                                'Content-Type': 'application/json',

                                'Authorization': 'Bearer ' + $scope.jwt_token,

                                'Accept': 'application/json',
                            }

                        }).then(function (data) {
                            $scope.natureColisListBc = data.data;
                        });
                    }();


                    $scope.qtionNotifstBc = [
                        {
                            value : "OUI"
                        },
                        {
                            value : "NON"
                        }
                    ];

                }
                else{
                    $state.go("mainpage.list-colis-journalier");
                }


                $scope.modifierArticle = function(event, item, check) {

                    if(check !== 5){

                        alertify.alert("<strong>Di-Gital web</strong> : Aucune modification du bon de reception de colis n'est autoris&eacute; a cet stade. La fiche a d&eacute;j&agrave; &eacute;t&eacute; valid&eacute;e par la soci&eacute;t&eacute; de livraison.");
                    }
                    else{

                        var modalInstance = $modal.open({

                            templateUrl: ROOT_URL.absolute + '/e-module/livraison/colis-journalier/edit-article-colis-journalier.html',

                            controller: 'InstanceEditArticleJournalier',

                            resolve: {
                                item: function () {
                                    return item || null;
                                }
                            }
                        });

                        modalInstance.result.then(function (result) {
                            console.log('result: ' + item);
                        }, function () {
                            $scope.findOne();
                        });
                    }
                }


                $scope.saveBasicInfo = function () {

                    $scope.loadingPage = true;

                    $http({

                        url: $scope.api + '/update-basic-info-de-la-fiche',

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

                                $state.go("mainpage.list-colis-journalier");

                            }
                            else if (data.data.information.requestCode === 400) {
                                alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);

                            }
                            else {

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


        .controller('InstanceEditArticleJournalier', function($scope, $modalInstance, item, $http, alertify, $location,
                                                   ROOT_URL, $state, $cookies, $log) {

            if($cookies.getObject('uData')){

                $scope.api              = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token        = $cookies.getObject('jw_token');
                $scope.jxes 		    = {};


                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };



                if (item !== null && item !=='') {

                    $scope.titleFrm = "Modification d'un article de la fiche de receuperation de colis";

                    $scope.style = "btn-outline-success";

                    $scope.nomBtn = "Appliquer modification"

                    $scope.jxes = item;

                }


                $scope.zoneListEditData = function () {

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api + '/find-all-zone-couverture-by-type/'+item.zoneLivraison.typeZoneCouverture.id,

                        method: "GET",

                        async: true,

                        cache: false,

                        headers: {

                            'Content-Type': 'application/json',

                            'Authorization': 'Bearer ' + $scope.jwt_token,

                            'Accept': 'application/json',
                        }

                    }).then(function (data) {

                        $scope.zoneRecuperationListBcEdit = data.data;
                    });
                }();


                $scope.typeZoneListEditData = function () {

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api + '/find-all-type-zone',

                        method: "GET",

                        async: true,

                        cache: false,

                        headers: {

                            'Content-Type': 'application/json',

                            'Authorization': 'Bearer ' + $scope.jwt_token,

                            'Accept': 'application/json',
                        }

                    }).then(function (data) {

                        $scope.typeZoneListEditData = data.data;
                    });
                }();


                $scope.jxes.prixLivraison = ""+item.prixLivraison;


                $scope.natureColisListBc = function () {

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api + '/find-all-type-colis',

                        method: "GET",

                        async: true,

                        cache: false,

                        headers: {

                            'Content-Type': 'application/json',

                            'Authorization': 'Bearer ' + $scope.jwt_token,

                            'Accept': 'application/json',
                        }

                    }).then(function (data) {
                        $scope.natureColisListBcEdit = data.data;
                    });
                }();


                $scope.qtionNotifstBc = [
                    {
                        value : "OUI"
                    },
                    {
                        value : "NON"
                    }
                ];


                $scope.init = function(){

                    $scope.tabPriceEdit = [];

                    for( var i=1000; i<=10000; i+=500){

                        $scope.tabPriceEdit.push(i);
                    }

                }();


                $scope.loadPerimetreEditData = function (data) {

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api + '/find-all-zone-couverture-by-type/'+data.id,

                        method: "GET",

                        async: true,

                        cache: false,

                        headers: {

                            'Content-Type': 'application/json',

                            'Authorization': 'Bearer ' + $scope.jwt_token,

                            'Accept': 'application/json',
                        }

                    }).then(function (data) {
                        $scope.zoneRecuperationListBcEdit = data.data;
                    });
                };


                $scope.saveArticleEditData = function () {

                    $scope.loadingPage = true;

                    $http({

                        url: $scope.api + '/update-article-de-la-fiche',

                        method: "PUT",

                        data: angular.toJson($scope.jxes),

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

                            }
                            else if (data.data.information.requestCode === 400) {
                                alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);

                            }
                            else {

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



        .controller('ListingColisJournalierRecuController', function ($rootScope, $scope, $location, $http, alertify, $state,
                                                                  $timeout, $cookies, ROOT_URL, $log, $modal, DTOptionsBuilder,
                                                                  DTColumnDefBuilder, $filter) {
            if($cookies.getObject('uData')){

                $scope.tab              = 1;
                $scope.donnees          = {};

                $scope.items            = {};
                $scope.printRecu        = {};
                $scope.api              = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token        = $cookies.getObject('jw_token');
                $scope.items.profile    = $cookies.getObject('uData').profile;
                var _this               = this;
                $scope.loadingPage      = false;

                //$log.info($scope.items.profile)


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
                    DTColumnDefBuilder.newColumnDef(8).notSortable()
                ];


                $scope.findall = function () {

                    $scope.loadingPage = true;

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-bon-de-commande-by-entreprise/"+$cookies.getObject('uData').profile.gareRoutiere.compagnie.id,

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
                            $scope.donnees = data.data;
                        },
                        function(data, status, xhrStatus){
                            $scope.loadingPage = false;
                        });
                };

                $scope.findall();

                $scope.setTabJrRecu = function(newTab){
                    $scope.tab = newTab;
                };


                $scope.isSetJrRecu = function(tabNum){
                    return $scope.tab === tabNum;
                };



                $scope.actionOneItemFichLivraison = function (id, item, action) {

                    if(action === 1){

                        alertify.okBtn("Oui").cancelBtn("Non").confirm("<strong>Di-Gital web</strong> - &Ecirc;tes-vous s&ucirc;r de vouloir supprimer l'enregistrement selectionn&eacute; ?",function(event){

                            if(event) {

                                $scope.loadingPage = true;

                                $http({

                                    url: $scope.api+"/delete-item-bon-de-commande",

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

                                    url: $scope.api+"/active-item-bon-de-commande",

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



                $scope.impressionFicheA = function (data) {

                    if(data){

                        $http({

                            dataType: 'jsonp',

                            url: $scope.api+"/find-one-fiche-bon-de-commande-by-ref/"+data.reference,

                            method: "GET",

                            async: true,

                            cache: false,

                            headers: {

                                'Content-Type': 'application/json',

                                'Authorization': 'Bearer ' + $scope.jwt_token,

                                'Accept': 'application/json',
                            }
                        })
                            .then(function(datas){

                                $scope.printRecu.articles                  = datas.data.bonDeCommandeList;

                                $scope.printRecu.logoPhoenix               = ROOT_URL.logoPhoenix;
                                $scope.printRecu.logoEntreprise            = ROOT_URL.pathImage+"/"+data.partenaire.entreprise.logo;
                                $scope.printRecu.arrierePlan               = ROOT_URL.arrierePlan;
                                $scope.printRecu.nomEntreprise             = data.partenaire.entreprise.compagnie;
                                $scope.printRecu.nomPartenaire             = data.partenaire.nomComplet;

                                $scope.printRecu.abbrev                    = data.partenaire.entreprise.abbrev.toUpperCase();
                                $scope.printRecu.compagnie                 = data.partenaire.entreprise.compagnie.toUpperCase();
                                $scope.printRecu.contact                   = data.partenaire.entreprise.contact;
                                $scope.printRecu.contactRespEntrep         = data.partenaire.entreprise.contactResponsable;
                                $scope.printRecu.adresse                   = data.partenaire.entreprise.adresse;
                                $scope.printRecu.rccm                      = data.partenaire.entreprise.rccm;
                                $scope.printRecu.email                     = data.partenaire.entreprise.email.toLowerCase();

                                $scope.printRecu.contactResponsable        = data.partenaire.contactResponsable;
                                $scope.printRecu.activite                  = data.partenaire.activite;
                                $scope.printRecu.ordre                     = data.partenaire.ordre;
                                $scope.printRecu.contactPart               = data.partenaire.contact;
                                $scope.printRecu.nomResponsable            = data.partenaire.nomResponsable;
                                $scope.printRecu.zoneCouvPart              = data.partenaire.zoneCouverture.typeZoneCouverture.typezone+" dans la commune de "+ data.partenaire.zoneCouverture.zoneCouverture + " plus précisement "+ data.partenaire.precisonZone.toUpperCase();

                                $scope.printRecu.reference                 = data.reference;
                                $scope.printRecu.typeLivraison             = data.typeLivraison;
                                $scope.printRecu.datelivraisonSouhaite     = moment(data.datelivraisonSouhaite, 'YYYY-MM-DD').format('DD/MM/YYYY');
                                $scope.printRecu.zoneRecuperation          = data.zoneRecuperation.zoneCouverture;
                                $scope.printRecu.typeZoneRecup             = data.zoneRecuperation.typeZoneCouverture.typezone;
                                $scope.printRecu.precisionZoneRecup        = data.precisionZoneRecup;
                                $scope.printRecu.montantGlobal             = data.montantGlobal;
                                $scope.printRecu.montantLivraisn           = data.montantGlobalLivraison;
                                $scope.printRecu.auteur                    = data.profile.nomPrenoms;
                                $scope.printRecu.etat                      = data.suiviDemande.suivi;

                                //$log.info($scope.printRecu);

                                _this.imprimerDocument("fiche-de-colis-journalier", false, $scope.printRecu, "fiche-de-colis-journalier"+data.ordre+".pdf");

                            });
                    }
                };

                $scope.impressionAllDataToFiche = function (donnes) {

                    if(donnes){

                        $scope.printRecu.logoPhoenix        = ROOT_URL.logoPhoenix;

                        $scope.printRecu.logoEntreprise     = ROOT_URL.pathImage+"/"+$scope.items.profile.gareRoutiere.compagnie.logo;

                        $scope.printRecu.auteur             = $scope.items.profile.nomPrenoms;

                        $scope.printRecu.list               = donnes;

                        _this.imprimerDocument("fiche-all-colis-journalier", false, $scope.printRecu, "fiche-de-colis-all-journalier"+$scope.items.profile.idDigital+".pdf");
                    }
                    else{
                        alertify.alert("<strong>Di-Gital web</strong> : Impression de la liste impossible. Pas de donn&eacute;es disponibles");
                    }

                }


                //generer un etat excel
                $scope.getExcelEtat =function(donnes, type){

                    if(type === 5){

                        $scope.printRecu.type    = "attente";
                    }
                    else if(type === 6){

                        $scope.printRecu.type    = "validé";
                    }
                    else if(type === 7){

                        $scope.printRecu.type    = "recup";
                    }
                    else if(type === 8){

                        $scope.printRecu.type    = "rejet";
                    }

                    $scope.printRecu.logoPhoenix        = ROOT_URL.logoPhoenix;

                    $scope.printRecu.logoEntreprise     = ROOT_URL.pathImage+"/"+$scope.items.profile.gareRoutiere.compagnie.logo;

                    $scope.printRecu.auteur             = $scope.items.profile.nomPrenoms;

                    $scope.printRecu.idEntreprise       = $scope.items.profile.gareRoutiere.compagnie.id;

                    $scope.printRecu.list               = donnes;

                    _this.exportExcel("export-excel", false, $scope.printRecu, "rapport.xls");
                }


                this.imprimerDocument = function (urlImpression, finParcours, data, fileName){

                    $scope.loadingPage = true;

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

                            $scope.loadingPage = false;

                        }
                        else{

                            var file = new Blob([response.data], {type: 'application/pdf'});

                            var fileURL = window.URL.createObjectURL(file);

                            var win = window.open(fileURL, '_blank');

                            if(win){
                                win.focus();
                            }
                            $scope.loadingPage = false;
                        }

                    }, function errorCallback(response) {
                        alertify.alert("<strong>Di-Gital web</strong> : Service non disponible");
                        $scope.loadingPage = false;
                    });
                }


                $scope.traiterDemande = function(event, item, check, index) {

                    var modalInstance = null;

                    if(index === 1){

                        if(check !== 5){

                            alertify.alert("<strong>Di-Gital web</strong> : Aucun traitement sur bon de reception de colis n'est autoris&eacute; a cet stade. La fiche a d&eacute;j&agrave; &eacute;t&eacute; valid&eacute;e par la soci&eacute;t&eacute; de livraison.");
                        }
                        else{

                            modalInstance = $modal.open({

                                templateUrl: ROOT_URL.absolute + '/e-module/livraison/colis-journalier/traitement-article-colis-journalier.html',

                                controller: 'InstanceTraiterDemande',

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

                    else if(index === 2){

                        if(check !== 6){

                            alertify.alert("<strong>Di-Gital web</strong> : Aucun traitement sur bon de reception de colis n'est autoris&eacute; a cet stade. La fiche a d&eacute;j&agrave; &eacute;t&eacute; valid&eacute;e par la soci&eacute;t&eacute; de livraison.");
                        }
                        else{

                            modalInstance = $modal.open({

                                templateUrl: ROOT_URL.absolute + '/e-module/livraison/colis-journalier/traitement-article-colis-journalier.html',

                                controller: 'InstanceTraiterDemande',

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


                }


                this.exportExcel = function (urlImpression, finParcours, data, fileName){

                    $scope.loadingPage = true;

                    var ieEDGE = navigator.userAgent.match(/Edge/g);

                    var ie =  navigator.userAgent.match(/.NET/g);

                    var oldIE =  navigator.userAgent.match(/MSIE/g);

                    $http({

                        method : "POST",

                        url : $scope.api+"/"+urlImpression,

                        responseType: 'arraybuffer',

                        data : data,

                        headers : {'Content-type' : 'application/json'}

                    }).then(function successCallback(response) {

                        if(ie || oldIE || ieEDGE){

                            var blob = new window.Blob([response.data],{type: 'application/vnd.ms-excel'});

                            window.navigator.msSaveBlob(blob,fileName);

                            $scope.loadingPage = false;

                        }
                        else{

                            var file = new Blob([response.data], {type: 'application/vnd.ms-excel'});

                            var fileURL = window.URL.createObjectURL(file);

                            var win = window.open(fileURL, '_blank');

                            if(win){

                                win.focus();
                            }

                            $scope.loadingPage = false;
                        }

                    }, function errorCallback(response) {
                        alertify.alert("<strong>Di-Gital web</strong> : Service non disponible");
                        $scope.loadingPage = false;
                    });
                }

            }
            else{
                $state.go("404");
            }
        })

        .controller('InstanceTraiterDemande', function($scope, $modalInstance, item, $http, alertify, $location,
                                                              ROOT_URL, $state, $cookies, $log, $filter) {

            if($cookies.getObject('uData')){

                $scope.api              = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
                $scope.jwt_token        = $cookies.getObject('jw_token');
                $scope.items 		    = {};
                $scope.tab              = 1;

                //$log.info($cookies.getObject('uData').profile);


                $scope.fermer = function() {
                    $modalInstance.dismiss('cancel');
                };

                $scope.setTabTraitement = function(newTab){
                    $scope.tab = newTab;
                };


                $scope.isSetTraitement = function(tabNum){
                    return $scope.tab === tabNum;
                };

                if (item !== null && item !=='') {

                    $scope.titleFrm = "Validation de la fiche de receuperation de colis n° "+item.ordre;

                    $scope.style = "btn-outline-success";

                    $scope.nomBtn = "Appliquer modification";

                    $scope.datelivraisonSouhaite = $filter('date')(item.datelivraisonSouhaite, 'dd/MM/yyyy');

                    $scope.items = item;

                }


                $scope.findAllBonCmde = function () {

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-one-fiche-bon-de-commande-by-ref/"+item.reference,

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

                        $scope.items.bonDeCommandeList = data.data.bonDeCommandeList;

                    },
                    function(data, status, xhrStatus){
                        $scope.loadingPage = false;
                    });
                }();

                $scope.statutDmdeListe = function () {

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api + '/find-all-suivi-demande-livraison/'+item.suiviDemande.id,

                        method: "GET",

                        async: true,

                        cache: false,

                        headers: {

                            'Content-Type': 'application/json',

                            'Authorization': 'Bearer ' + $scope.jwt_token,

                            'Accept': 'application/json',
                        }

                    }).then(function (data) {

                        $scope.statutDmdeListe = data.data;
                    });
                }();


                $scope.traiterBon = function () {

                    $scope.loadingPage = true;

                    $http({

                        url: $scope.api + '/traitement-du-bon-soumis',

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

                        }
                        else if (data.data.information.requestCode === 400) {
                            alertify.alert("<strong>Di-Gital web</strong> : " + data.data.information.message);

                        }
                        else {

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