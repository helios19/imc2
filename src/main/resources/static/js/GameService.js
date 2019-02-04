
'use strict';
 
angular.module('rockPaperScissors').factory('GameService',
    ['$localStorage', '$http', '$q', 'urls',
        function ($localStorage, $http, $q, urls) {

            var factory = {
                playRps: playRps
            };
 
            return factory;

             function playRps(game) {
                console.log('Fetching all games given player symbol:' + game.playerSymbol + ' and game uuid:' + game.uuid);
                var deferred = $q.defer();
                var uuid = game.uuid !== undefined ? game.uuid : '';
                $http.get(urls.GAME_SERVICE_API + '/' + game.playerSymbol + '/' + uuid)
                    .then(
                        function (response) {
                            console.log('Fetched successfully customer games');
                            $localStorage.previousGames = response.data.history;
                            deferred.resolve(response);
                        },
                        function (errResponse) {
                            console.error('Error while loading games');
                            deferred.reject(errResponse);
                        }
                    );
                return deferred.promise;
             }
        }
    ]);