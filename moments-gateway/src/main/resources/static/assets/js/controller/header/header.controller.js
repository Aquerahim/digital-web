
(function() {

    'use strict';

    DiGital

        .controller('HeaderController', function ($rootScope, $scope, $location, ROOT_URL, $http, $state, $cookies, alertify, $log) {

            console.clear();

            if($cookies.getObject('uData')){

                //$log.info($cookies.getObject('uData'))

                //$scope.versionCode = "v 2021.12.0.6";

                $scope.api = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;

                $scope.jwt_token = $cookies.getObject('jw_token');

                $scope.welcome = $cookies.getObject('uData').profile.nomPrenoms;

                $scope.username = $cookies.getObject('uData').username;

                $scope.smsId = $cookies.getObject('uData').profile.gareRoutiere.compagnie.smsCredential.id;


                $scope.loGout = function(){

                    alertify.okBtn("Oui").cancelBtn("Non").confirm("Souhaitez-vous d&eacute;connecter de l'application ? Nous rappellons que cette op&eacute;ration permettra par la m&ecirc;me occasion la reinitialisation de toute les cookies et pr&eacute;f&eacute;rences",function(event){

                        if(event) {

                            $http({

                                dataType: 'jsonp',

                                url: $scope.api+"/notification-deconnexion/"+$cookies.getObject('uData').profile.idDigital,

                                method: "GET",

                                async: true,

                                cache: false,

                                headers: {

                                    'Content-Type': 'application/json',

                                    'Authorization': 'Bearer ' + $scope.jwt_token,

                                    'Accept': 'application/json',
                                }
                            });

                            $cookies.remove('uData');

                            $state.go("login");
                        }
                    });
                }


                $scope.lockedIn = function(){

                    alertify.okBtn("Oui").cancelBtn("Non").confirm("Souhaitez-vous verrouiller votre session ?",function(event){

                        if(event) {

                            $state.go("session-verouille");
                        }
                    });
                }


                $scope.checkSms = function () {

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/checking-sms/"+$scope.smsId,

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

                        $scope.nbrMsgRestant = data.data;
                    },
                    function(data, status, xhrStatus){

                        $scope.nbrMsgRestant = 0;
                    });
                };
                $scope.checkSms();


                $scope.findAllNotifSysteme = function () {

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-notification-by-profile/"+$cookies.getObject('uData').profile.idDigital,

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
                        $scope.notif = data.data;
                    });
                };
                $scope.findAllNotifSysteme();
            }
            else{
                $state.go("404");
            }


        })
        .controller('notificationController', function ($rootScope, $scope, $location, ROOT_URL, $http, $state, $cookies, alertify, $log) {

            if($cookies.getObject('uData')){

                $scope.api = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;

                $scope.jwt_token = $cookies.getObject('jw_token');

                $scope.findAll = function () {

                    $http({

                        dataType: 'jsonp',

                        url: $scope.api+"/find-all-notification-by-profile/"+$cookies.getObject('uData').profile.idDigital,

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

                        $scope.datas = data.data;
                    });
                };
                $scope.findAll();
            }
            else{
                $state.go("404");
            }
        });
})();