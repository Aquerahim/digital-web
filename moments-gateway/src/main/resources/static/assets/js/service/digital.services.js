(function() {
    'use strict';

    angular.module('digital.services', [])

        .factory('AuthenticationService', function($http) {

            return {

                find : function(login, password, api) {

                    return $http({

                        method: "POST",

                        url: api+"/find-one-profile",

                        data: {

                            "username": login,

                            "password": password
                        }
                    });
                },


                auth: function(login, password, api) {

                    return $http({

                        method: "POST",

                        url: api+"/authentication",

                        data: {

                            "username": login,

                            "password": password
                        }
                    });
                },


                updated: function(login, password, api) {

                    return $http({

                        method: "PUT",

                        url: api+"/update-password",

                        data: {

                            "username": login,

                            "password": password
                        }
                    });
                },


                lastConnection: function(login, api) {

                    return $http({

                        method: "PUT",

                        url: api+"/last-connection",

                        data: {
                            "username": login,

                            "password": "****#***@***"
                        }
                    });
                },


                codeUpdatePwd : function(login, api, jwt_token) {

                    return $http({

                        method: "POST",

                        url: api+"/get-code-to-update-password",

                        data: {

                            "username": login
                        },

                        headers: {

                            'Content-Type': 'application/json',

                            'Authorization': 'Bearer ' + jwt_token,

                            'Accept': 'application/json',
                        }
                    });
                },


                checking: function(items, api) {

                    return $http({

                        method: "POST",

                        url: api+"/verifi-user",

                        data: {

                            "email": items.email
                        }
                    });
                },


                reset: function(items, api) {

                    return $http({

                        method: "PUT",

                        url: api+"/reset-password",

                        data: {
                            "email": items.email,

                            "telephone": items.telephone
                        }
                    });
                },


                /*codeOTP: function(login, api) {

                    return $http({

                        method: "PUT",

                        url: api+"/send-code-otp-connexion",

                        data: {
                            "username": login
                        }
                    });
                }*/
            };
        });

})();