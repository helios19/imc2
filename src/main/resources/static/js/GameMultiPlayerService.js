
'use strict';
 
angular.module('rockPaperScissors').factory('GameMultiPlayerService',
    ['$localStorage', '$http', '$q', 'urls',
        function ($localStorage, $http, $q, urls) {

            var factory = {
                playRps: playRps
            };
 
            return factory;

             function playRps(game) {
                console.log('Fetching all games given player symbols:' + game.playerSymbols + ' and game uuid:' + game.uuid);
                var deferred = $q.defer();
                var uuid = game.uuid !== undefined ? game.uuid : '';
                var params = queryString(game.playerSymbols, 'playerSymbol');
                $http.get(urls.GAME_MULTIPLAYER_SERVICE_API + '/' + uuid + '?' + params)
                    .then(
                        function (response) {
                            console.log('Fetched successfully game result');
                            $localStorage.previousGames = response.data.history;
                            deferred.resolve(response);
                        },
                        function (errResponse) {
                            console.error('Error while loading game result');
                            deferred.reject(errResponse);
                        }
                    );
                return deferred.promise;
             }

             function queryString(params, paramName) {

                 return params.map(function(el) {
                   return paramName + '=' + el;
                 }).join('&');

//                 return Object.keys(params).map(function(key) {
//                     return key + '=' + params[key]
//                 }).join('&');
             }
        }
    ]);