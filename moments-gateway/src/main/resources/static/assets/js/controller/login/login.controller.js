(function() {

    'use strict';

    DiGital

        .controller('LoginController', function ($rootScope, $scope, $location, $http, alertify, $state,
                                                 AuthenticationService, $timeout, $cookies, ROOT_URL, $log, $crypto) {

            $cookies.remove('uData');
            $cookies.remove('digiapps');
            $cookies.remove('uChoice');
            $cookies.remove('uClient');
            $cookies.remove('uZoneLivraison');
            $cookies.remove('uLivreur');
            $scope.frmConnect   = true;
            $scope.disabled     = false;
            $scope.loadingPage  = false;
            $scope.firstCnx     = false;
            $scope.codeOTP      = false;
            $scope.forgetPwd    = false;
            $scope.message      = null;
            $scope.viewBtn      = true;
            $scope.items        = {};
            $scope.user         = {};
            $scope.api          = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;


            $scope.getCodeOTP = function () {

                $scope.loadingPage = true;

                return AuthenticationService

                    .find($scope.items.username.toUpperCase(), $scope.items.password, $scope.api)

                    .then(

                        function(response) {

                            var date = new Date();

                            //add 30 minutes to date
                            date.setTime(date.getTime() + (180* 60 * 1000));

                            $scope.forgetPwd = true;

                            if(parseInt(response.status) === 200){

                                $scope.loadingPage = false;

                                $cookies.putObject('uData', response.data.user,  {'expires' : date});

                                $scope.viewBtn = false;

                                //$scope.codeOTP = true;

                                if(response.data.user.firstConnexion === 1){

                                    $log.info("STAUT UTILISATEUR ::::::::::::  JE SUIS UN NOUVEAU DE LA MAISON | DI-GITAL Web");
                                    $scope.firstCnx = true;
                                    $scope.items.codeOTP = response.data.codeOTP;
                                }
                                else{

                                    $scope.items.codeOTP = response.data.codeOTP;
                                }
                            }
                            else {

                                $scope.loadingPage = false;
                                alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + response.data.information.message);
                            }
                        },
                        function (response) {
                            $scope.loadingPage = false;
                            if(parseInt(response.status) === 400){

                                alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : " + response.data.information.message);
                            }
                            else{
                                alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> - TIME OUT : Connexion impossible pri&egrave;re verifier votre connexion ou vos identifiants de connexion.");
                            }
                        }
                    );
            }


            $scope.forgetFrm = function (frm) {

                $scope.frmConnect = !frm;
            };



            var reg = new RegExp("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\$%\^&\*])(?=.{8,})");

            $scope.updatePassword = function() {

                $log.info("INFO ::::::::::::  NOUVEAU UTILSIATEUR : MISE A JOUR DU MOT DE PASSE");

                $scope.loadingPage = true;

                $scope.user.nouveauMotDePasse = $scope.items.nouveauMotDePasse;

                $scope.user.pwd = $scope.items.password;

                if($scope.items.password !=null && $scope.user.nouveauMotDePasse){

                    if($scope.items.password === $scope.user.nouveauMotDePasse){

                        $scope.loadingPage = false;

                        alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : Le mot de passe est identique a l'ancien mot de passe.");
                    }
                    else{

                        if (reg.test($scope.user.nouveauMotDePasse) === true) {

                            var pwd = $scope.user.nouveauMotDePasse;

                            var login = $scope.items.username;

                            var route = $scope.api;

                            if(parseInt($scope.codeOTP) === parseInt($scope.items.codeOTP)){

                                return AuthenticationService

                                    .updated(login, pwd, route)

                                    .then(function(response) {

                                            var date = new Date();

                                            //add 30 minutes to date
                                            date.setTime(date.getTime() + (180* 60 * 1000));

                                            $scope.forgetPwd = true;

                                            if(response.status === 200){

                                                if(parseInt(response.data.information.requestCode) === 200){

                                                    //Mise a jour de last connexion
                                                    return AuthenticationService.lastConnection($scope.items.username.toUpperCase(), $scope.api)

                                                        .then(function(response) {

                                                                if(parseInt(response.status) === 200){

                                                                    $scope.loadingPage = false;

                                                                    $cookies.putObject('jw_token', response.data.token,  {'expires' : date});

                                                                    $cookies.putObject('pwd',$crypto.encrypt(pwd));

                                                                    $state.go("mainpage.dashboard");
                                                                }
                                                                else{

                                                                    $scope.loadingPage = false;

                                                                    alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : Echec de la connexion Username ou Mot de passe invalide.");
                                                                }
                                                            },
                                                            function (response) {

                                                                $scope.loadingPage = false;

                                                                alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : "+response.data.information.message);
                                                            }
                                                        );
                                                }
                                                else{

                                                    $scope.loadingPage = false;

                                                    alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : "+response.data.information.message);
                                                }
                                            }
                                            else{

                                                $scope.loadingPage = false;

                                                alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : "+response.data.information.message);
                                            }
                                        },
                                        function (response) {

                                            $scope.loadingPage = false;

                                            alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : "+response.data.information.message);
                                        }
                                    );
                            }
                            else{

                                $scope.loadingPage = false;

                                alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> <br>Le Code One-Time Password (OTP) a usage unique est incorrect.");
                            }
                        }
                        else{

                            $scope.loadingPage = false;

                            alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : Le nouveau mot de passe ne respect pas la politique de l'application.");
                        }
                    }
                }
                else{

                    $scope.loadingPage = false;

                    alertify.okBtn("OK").alert("<strong>Di-Gital web</strong> : Impossible de mettre &agrave; jour votre mot de passe.");
                }
            };


            /**
             * Authentifier l'utilisateur
             */
            $scope.toLoginAccount = function() {

                $scope.loadingPage  = true;

                if(parseInt($scope.codeOTP) === parseInt($scope.items.codeOTP)){

                    return AuthenticationService.lastConnection($scope.items.username.toUpperCase(), $scope.api)

                        .then(function(response) {

                                $scope.loadingPage = false;

                                if(parseInt(response.status) === 200){

                                    var date = new Date();

                                    //add 30 minutes to date
                                    date.setTime(date.getTime() + (180* 60 * 1000));

                                    $cookies.putObject('jw_token', response.data.token,  {'expires' : date});

                                    $cookies.putObject('pwd',$crypto.encrypt($scope.items.password));

                                    $state.go("mainpage.dashboard");
                                }
                                else{
                                    alertify.alert("<strong>Di-Gital web</strong> : Echec de la connexion Username ou Mot de passe invalide.");
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

                    alertify.alert("<strong>Di-Gital web</strong> <br>Le Code One-Time Password (OTP) a usage unique incorrect.");
                }
            }
        });
})();