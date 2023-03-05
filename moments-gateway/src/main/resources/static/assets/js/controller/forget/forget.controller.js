(function() {

    'use strict';

    DiGital

        .controller('ForgetPasswordController', function ($rootScope, $scope, $location, $http, alertify, $state,
                                                          AuthenticationService, $timeout, $cookies, ROOT_URL, $log) {

            $cookies.remove('digiapps');
            $scope.frmConnect   = true;
            $scope.found        = false;
            $scope.items        = {};
            $scope.api          = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;


            $scope.forgetFrm = function (frm) {
                $scope.frmConnect = !frm;
            };

            $scope.checkingUserdata = function () {

                console.clear();

                $scope.loadingPage = true;

                return AuthenticationService

                    .checking($scope.items, $scope.api)

                    .then(

                        function(response) {

                            $scope.loadingPage = false;

                            if(parseInt(response.status) === 200){

                                //$log.info(response.data)
                                //$log.info(response.data.telephone)

                                $scope.found              = true;

                                $scope.code               = response.data.codeOTP;

                                $scope.items.telephone    = response.data.telephone;
                            }
                            else {

                                alertify.alert("<strong>Di-Gital web</strong> : " + response.data.information.message);
                            }
                        },
                        function (response) {
                            $scope.loadingPage = false;
                            alertify.alert("<strong>Di-Gital web</strong> - TIME OUT : Connexion impossible pri&egrave;re verifier votre connexion ou vos identifiants de connexion.");
                        }
                    );
            };


            $scope.resetPassword = function () {

                $log.info("INFO ::::::::::::  REQUEST FOR USER : UTILISATEUR FAIT LA VERIFICATION PAR CODE OTP");

                $scope.loadingPage = true;

                $log.info($scope.items)

                if(parseInt($scope.code) === parseInt($scope.items.codeOTP)){

                    return AuthenticationService

                        .reset($scope.items, $scope.api)

                        .then(function(response) {

                                $scope.loadingPage = false;

                                if(response.status === 200){

                                    if(parseInt(response.status) === 200){

                                        $state.go("recoverpwsucces");
                                    }
                                    else if(parseInt(response.status) === 400){

                                        alertify.alert("<strong>Di-Gital web</strong> : "+response.data.information.message);
                                    }
                                    else{

                                        alertify.alert("<strong>Di-Gital web</strong> : "+response.data.information.message);
                                    }
                                }
                                else{

                                    alertify.alert("<strong>Di-Gital web</strong> : "+response.data.information.message);
                                }
                            },
                            function (response) {

                                $scope.loadingPage = false;

                                alertify.alert("<strong>Di-Gital web</strong> : "+response.data.information.message);
                            }
                        );
                }
                else{

                    $scope.loadingPage = false;

                    alertify.alert("<strong>Di-Gital web</strong> <br>Le Code One-Time Password (OTP) a usage unique est incorrect.");
                }
            };

        });
})();