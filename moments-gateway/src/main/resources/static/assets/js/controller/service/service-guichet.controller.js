(function() {

    'use strict';

    DiGital

        .controller('GuichetVoyageController', function ($rootScope, $scope, $location, $http, alertify, $state,
                                                               $timeout, $cookies, ROOT_URL, $log, $modal,
                                                         DTOptionsBuilder, DTColumnDefBuilder) {
            $cookies.remove('digiapps');

            $scope.api = $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
            $scope.loadingPagePage = false;

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
                    "sInfo": 'Trouvé : _TOTAL_ enregistrement(s)',
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


            $scope.initialize = function () {

                $scope.loadingPage = true;

                $http({

                    dataType: 'jsonp',

                    url: $scope.api+"/find-all-liste-des-rendez-vous-patient/",

                    method: "GET",

                    async: true,

                    cache: false,

                    headers: {

                        'Content-Type': 'application/json',

                        //'Authorization': 'Bearer ' + $scope.jwt_token,

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
            $scope.initialize();


            $scope.openFrmGuichetVoyage = function(event, item) {

                var modalInstance = $modal.open({

                    templateUrl: ROOT_URL.absolute + '/services/guichet/frm-ticket-de-voyage.html',
                    controller: 'InstanceGuichetVoyage',
                    resolve: {
                        item: function () {
                            return item || null;
                        }
                    }
                });

                modalInstance.result.then(function (result) {
                    console.log('result: ' + item);
                }, function () {
                    $scope.initialize();
                });
            }
        })

        .controller('InstanceGuichetVoyage', function($scope, $modalInstance, item, $http, alertify, $location,
                                                          ROOT_URL, $state, $cookies, $log) {

            $scope.api 			= $location.protocol()+"://" + $location.host() +':' +$location.port()+ ""+ROOT_URL.api;
            $scope.style 		= "btn-success";
            $scope.items 		= {};
            $scope.nomBtn 		= "Valider colis";
            $scope.titleFrm     = "Achat d'un nouveau ticket de voyage";
            $scope.actived = true;
            $scope.items.fraisDigital = 0;
            $scope.items.fraisSMS = 0;
            $scope.items.tarifTicket = 0;

            $scope.fermer = function() {
                $modalInstance.dismiss('cancel');
            };

            $scope.typePaiementList = [
                {
                    key : "PE",
                    value : "PAIEMENT EN ESPECE"
                },
                {
                    key : "PL",
                    value : "PAIMENT DIGITAL OU MOBILE"
                }
            ];

            $scope.typeVoyageList = [
                {
                    key : "A",
                    value : "ALLER-SIMPLE"
                },
                {
                    key : "AR",
                    value : "ALLER-RETOUR"
                }
            ];

            $scope.checkingTravel = function (obj) {
                $scope.actived = obj ? obj !== "AR" : true;

                switch (obj) {
                    case "AR":
                        $scope.items.fraisDigital = 1000;
                        break;
                    default:
                        $scope.items.fraisDigital = 500;
                        break;
                }
                $scope.items.fraisSMS = 100;
            };

            $scope.payTicket = function (obj) {
                if(obj === "PL"){
                    alertify.alert("le mode de PAIMENT DIGITAL OU MOBILE mettra automatiquement le ticket sous réserve de validation du paiement avec la réfénce de paiement.");
                }
                $scope.items.refTicket = obj+"-"+getRandomSpan(6);
            };


            $scope.villes = [
                { index: 1, label: 'Abengourou' },
                { index: 2, label: 'Abidjan' },
                { index: 3, label: 'Aboisso' },
                { index: 4, label: 'Abongoua' },
                { index:5,label: 'Adaou' },
                { index:6,label: 'Adiaké' },
                { index:7,label: 'Adjouan' },
                { index:8,label: 'Adzopé' },
                { index:9,label: 'Agbaou' },
                { index:10,label: 'Agboville' },
                { index:11,label: 'Agnibilékrou' },
                { index:12,label: 'Ahouanou' },
                { index:13,label: 'Ahoutoué' },
                { index:14,label: 'Akouédo' },
                { index:15,label: 'Akoupé' },
                { index:16,label: 'Alépé' },
                { index:17,label: 'Alounamouénou' },
                { index:18,label: 'Ananda (Daoukro)' },
                { index:19,label: 'Ananguié (Adzopé)' },
                { index:20,label: 'Annépé' },
                { index:21,label: 'Anyama' },
                { index:22,label: 'Arrah' },
                { index:23,label: 'Assaoufoué' },
                { index:24,label: 'Attiégouakro' },
                { index:25,label: 'Attoutou' },
                { index:26,label: 'Azaguié' },
                { index:27,label: 'Bacanda' },
                { index:28,label: 'Badikaha' },
                { index:29,label: 'Bako' },
                { index:30,label: 'Baléko' },
                { index:31,label: 'Bambalouma' },
                { index:32,label: 'Bandakagni-Sokoura' },
                { index:33,label: 'Bangolo' },
                { index:34,label: 'Bangoua' },
                { index:35,label: 'Banneu' },
                { index:36,label: 'Batéguédia II' },
                { index:37,label: 'Bazra-Nattis' },
                { index:38,label: 'Bécouéfin' },
                { index:39,label: 'Béoumi' },
                { index:40,label: 'Bettié' },
                { index:41,label: 'Biankouma' },
                { index:42,label: 'Biéby' },
                { index:43,label: 'Bin-Houyé' },
                { index:44,label: 'Blapleu' },
                { index:45,label: 'Bléniméouin' },
                { index:46,label: 'Blességué' },
                { index:47,label: 'Bloléquin' },
                { index:48,label: 'Boahia' },
                { index:49,label: 'Bocanda' },
                { index:50,label: 'Bogouiné' },
                { index:51,label: 'Boli' },
                { index:52,label: 'Bondo' },
                { index:53,label: 'Bongo' },
                { index:54,label: 'Bongouanou' },
                { index:55,label: 'Bonoua' },
                { index:56,label: 'Boromba' },
                { index:57,label: 'Botro' },
                { index:58,label: 'Bouaflé' },
                { index:59,label: 'Bouandougou' },
                { index:60,label: 'Bougousso' },
                { index:61,label: 'Bouna' },
                { index:62,label: 'Boundiali' },
                { index:63,label: 'Brofodoumé' },
                { index:64,label: 'Céchi' },
                { index:65,label: 'Dabéko' },
                { index:66,label: 'Dabou' },
                { index:67,label: 'Dabouyo' },
                { index:68,label: 'Dah-Zagna' },
                { index:69,label: 'Dakpadou' },
                { index:70,label: 'Daleu' },
                { index:71,label: 'Daloa' },
                { index:72,label: 'Danané' },
                { index:73,label: 'Danguira' },
                { index:74,label: 'Daoukro' },
                { index:75,label: 'Diabo' },
                { index:76,label: 'Diamarakro' },
                { index:77,label: 'Diangobo (Yakassé-Attobrou)' },
                { index:78,label: 'Diawala' },
                { index:79,label: 'Diboké' },
                { index:80,label: 'Didiévi' },
                { index:81,label: 'Diéouzon' },
                { index:82,label: 'Digbeugnoa' },
                { index:83,label: 'Dignago' },
                { index:84,label: 'Dikouehipalegnoa' },
                { index:85,label: 'Dimbokro' },
                { index:86,label: 'Diogo' },
                { index:87,label: 'Dioulatiédougou' },
                { index:88,label: 'Divo' },
                { index:89,label: 'Djouroutou' },
                { index:90,label: 'Doba' },
                { index:91,label: 'Dogbo' },
                { index:92,label: 'Doké' },
                { index:93,label: 'Domaboué' },
                { index:94,label: 'Domangbeu' },
                { index:95,label: 'Douasso' },
                { index:96,label: 'Doubé' },
                { index:97,label: 'Doudoukou' },
                { index:98,label: 'Duékoué' },
                { index:99,label: 'Ebounou' },
                { index:100,label: 'Elima' },
                { index:101,label: 'Ery-Macouguié' },
                { index:102,label: 'Fadiadougou' },
                { index:103,label: 'Fahandougou' },
                { index:104,label: 'Fahani' },
                { index:105,label: 'Fakaha' },
                { index:106,label: 'Fala' },
                { index:107,label: 'Famienkro' },
                { index:108,label: 'Faradiani' },
                { index:109,label: 'Farandougou' },
                { index:110,label: 'Ferkessédougou' },
                { index:111,label: 'Fodio' },
                { index:112,label: 'Fonondara' },
                { index:113,label: 'Fresco' },
                { index:114,label: 'Gabiadji' },
                { index:115,label: 'Gagnoa' },
                { index:116,label: 'Ganaoni' },
                { index:117,label: 'Gbambiasso' },
                { index:118,label: 'Gbangbégouiné' },
                { index:119,label: 'Gbangbégouiné-Yati' },
                { index:120,label: 'Gbékékro' },
                { index:121,label: 'Gbéléban' },
                { index:122,label: 'Gbémou' },
                { index:123,label: 'Gbogui' },
                { index:124,label: 'Gbon' },
                { index:125,label: 'Gbongaha' },
                { index:126,label: 'Gnaliepa' },
                { index:127,label: 'Gnangnon' },
                { index:128,label: 'Gohouo-Zagna' },
                { index:129,label: 'Gomon' },
                { index:130,label: 'Gonaté' },
                { index:131,label: 'Gouessesso' },
                { index:132,label: 'Gouiné' },
                { index:133,label: 'Goulia' },
                { index:134,label: 'Grabo' },
                { index:135,label: 'Grand-Bassam' },
                { index:136,label: 'Grand-Béréby' },
                { index:137,label: 'Grand-Lahou' },
                { index:138,label: 'Grand-Morié' },
                { index:139,label: 'Grand-Zattry' },
                { index:140,label: 'Guessabo' },
                { index:141,label: 'Guéyo' },
                { index:142,label: 'Guiendé' },
                { index:143,label: 'Guiglo' },
                { index:144,label: 'Guinglo-Tahouaké' },
                { index:145,label: 'Issia' },
                { index:146,label: 'Jacqueville' },
                { index:147,label: 'Kahin-Zarabaon' },
                { index:148,label: 'Kanakono' },
                { index:149,label: 'Kaniéné' },
                { index:150,label: 'Kanitélégué' },
                { index:151,label: 'Kanoroba' },
                { index:152,label: 'Kantélégué' },
                { index:153,label: 'Kanzra' },
                { index:154,label: 'Kaouara' },
                { index:155,label: 'Karakoro' },
                { index:156,label: 'Karakpo' },
                { index:157,label: 'Kasséré' },
                { index:158,label: 'Katiali' },
                { index:159,label: 'Katiéré' },
                { index:160,label: 'Katiola' },
                { index:161,label: 'Kimbirila-Sud' },
                { index:162,label: 'Koboko' },
                { index:163,label: 'Kodiokofi' },
                { index:164,label: 'Kofiplé' },
                { index:165,label: 'Kolia' },
                { index:166,label: 'Kong' },
                { index:167,label: 'Kongasso' },
                { index:168,label: 'Koni' },
                { index:169,label: 'Konolo' },
                { index:170,label: 'Korhogo' },
                { index:171,label: 'Koro' },
                { index:172,label: 'Kossou' },
                { index:173,label: 'Kouakro' },
                { index:174,label: 'Kouan-Houle' },
                { index:175,label: 'Kouassi-Blékro' },
                { index:176,label: 'Koukourandoumi' },
                { index:177,label: 'Kounoumon' },
                { index:178,label: 'Kouto' },
                { index:179,label: 'Kpata' },
                { index:180,label: 'Lahou-Kpanda' },
                { index:181,label: 'Lakota' },
                { index:182,label: 'Languibonou' },
                { index:183,label: 'Lataha' },
                { index:184,label: 'Liliyo' },
                { index:185,label: 'Lodala' },
                { index:186,label: 'Logoualé' },
                { index:187,label: 'Logouhi' },
                { index:188,label: 'Lohouré' },
                { index:189,label: 'Lokoligou' },
                { index:190,label: 'Lolobo (Yamoussoukro)' },
                { index:191,label: 'Lomokankro' },
                { index:192,label: 'Loplé' },
                { index:193,label: 'Lossingué' },
                { index:194,label: 'Lotono' },
                { index:195,label: 'Loupala' },
                { index:196,label: 'Loupougo' },
                { index:197,label: 'Loviguié' },
                { index:198,label: "M'bahiakro" },
                { index:199,label: "N'Dara" },
                { index:200,label: "N'douci"},
                { index:201,label: 'Nafana (Prikro)' },
                { index:202,label: 'Nagou' },
                { index:203,label: 'Nahio' },
                { index:204,label: 'Nandala' },
                { index:205,label: 'Nangbolodougou' },
                { index:206,label: 'Nassian' },
                { index:207,label: 'Natio' },
                { index:208,label: "N'Gokro" },
                { index:209,label: 'Niagbrahio' },
                { index:210,label: 'Niakaramandougou' },
                { index:211,label: 'Niambézaria' },
                { index:212,label: 'Niandono' },
                { index:213,label: 'Niangboué' },
                { index:214,label: 'Niangboué rivière' },
                { index:215,label: 'Nianzongo' },
                { index:216,label: 'Niellé' },
                { index:217,label: 'Niempurgué' },
                { index:218,label: 'Nigui-saff' },
                { index:219,label: 'Ninioro' },
                { index:220,label: 'Niofoin' },
                { index:221,label: 'Niorouhio' },
                { index:222,label: 'Nitiadougou' },
                { index:223,label: 'Nodiahan' },
                { index:224,label: 'Nofou' },
                { index:225,label: 'Nomparadougou' },
                { index:226,label: 'Nondara' },
                { index:227,label: 'Noonlara' },
                { index:228,label: 'Nouamou' },
                { index:229,label: 'Odienné' },
                { index:230,label: 'Olodio' },
                { index:231,label: 'Oress-Krobou' },
                { index:232,label: 'Ouangolodougou' },
                { index:233,label: 'Ouaragahio' },
                { index:234,label: 'Ouarapa' },
                { index:235,label: 'Ouattaradougou' },
                { index:236,label: 'Ouazomon' },
                { index:237,label: 'Ouéllé' },
                { index:238,label: 'Ouendé-Kouassikro' },
                { index:239,label: 'Oumé' },
                { index:240,label: 'Oupoyo' },
                { index:241,label: 'Ouyably-Gnondrou' },
                { index:242,label: 'Pacobo' },
                { index:243,label: 'Panadougou' },
                { index:244,label: 'Pelezi' },
                { index:245,label: 'Pinhou' },
                { index:246,label: 'Podiagouine' },
                { index:247,label: 'Ponadongou' },
                { index:248,label: 'Poniakélé' },
                { index:249,label: 'Ponondougou' },
                { index:250,label: 'Portio' },
                { index:251,label: 'Poundiou' },
                { index:252,label: 'Prikro' },
                { index:253,label: 'Rubino' },
                { index:254,label: 'Sakassou' },
                { index:255,label: 'Samatiguila' },
                { index:256,label: 'San-Pédro' },
                { index:257,label: 'Sandougou-Soba' },
                { index:258,label: 'Santa (Biankouma)' },
                { index:259,label: 'Sassandra' },
                { index:260,label: 'Segana' },
                { index:261,label: 'Séguéla' },
                { index:262,label: 'Séguelon' },
                { index:263,label: 'Seleho' },
                { index:264,label: 'Sianhala' },
                { index:265,label: 'Siempurgo' },
                { index:266,label: 'Sikensi' },
                { index:267,label: 'Sinématiali' },
                { index:268,label: 'Sinfra' },
                { index:269,label: 'Sirasso' },
                { index:270,label: 'Sodalako' },
                { index:271,label: 'Sokoro' },
                { index:272,label: 'Songon' },
                { index:273,label: 'Soubré' },
                { index:274,label: 'Tabayo 1' },
                { index:275,label: 'Taboitien' },
                { index:276,label: 'Tabou' },
                { index:277,label: 'Tafiré' },
                { index:278,label: 'Tahiraguhé' },
                { index:279,label: 'Talaho' },
                { index:280,label: 'Teapleu' },
                { index:281,label: 'Ténélogo' },
                { index:282,label: 'Tengréla (ville)' },
                { index:283,label: 'Tiagba' },
                { index:284,label: 'Tiapoum' },
                { index:285,label: 'Tiassalé' },
                { index:286,label: 'Tie Ndiékro' },
                { index:287,label: 'Tiébissou' },
                { index:288,label: 'Tiédio' },
                { index:289,label: 'Tiémé' },
                { index:290,label: 'Tienko' },
                { index:291,label: 'Tinhou' },
                { index:292,label: 'Togoniéré' },
                { index:293,label: 'Tonla' },
                { index:294,label: 'Tortiya' },
                { index:295,label: 'Tougbo' },
                { index:296,label: 'Toulepleu' },
                { index:297,label: 'Toumo (Boundiali)' },
                { index:298,label: 'Toumodi' },
                { index:299,label: 'Toumoukoro' },
                { index:300,label: 'Tounvré' },
                { index:301,label: 'Vavoua' },
                { index:302,label: 'Voueboufla' },
                { index:303,label: 'Waraniéné' },
                { index:304,label: 'Womon' },
                { index:305,label: 'Yakassé-Attobrou' },
                { index:306,label: 'Yakassé-Mé' },
                { index:307,label: 'Yama (Boundiali)' },
                { index:308,label: 'Yamoussoukro' },
                { index:309,label: 'Yaou' },
                { index:310,label: 'Yapleu' },
                { index:311,label: 'Yorodougou' },
                { index:312,label: 'Zéo' },
                { index:313,label: 'Ziasso' },
                { index:314,label: 'Ziédougou' },
                { index:315,label: 'Zonneu' },
                { index:316,label: 'Zou' },
                { index:317,label: 'Zouan-Hounien' },
                { index:318,label: 'Zoukougbeu' },
                { index:319,label: 'Zuénoula' },
            ];
            $scope.text2 = '';
            $scope.minlength = 1;
            $scope.selected = {};


            function getRandomSpan(length) {
                var result           = '';
                var characters       = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ01234567899874563210';
                var charactersLength = characters.length;
                for ( var i = 0; i < length; i++ ) {
                    result += characters.charAt(Math.floor(Math.random() * charactersLength));
                }
                return result;
            }

        });
})();