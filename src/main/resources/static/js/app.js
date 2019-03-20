
var app = angular.module('rockPaperScissors',['ui.router','ngStorage','ui.bootstrap']);

app.constant('urls', {
    BASE: 'http://localhost:8080/',
    GAME_SERVICE_API : 'http://localhost:8080/rock-paper-scissors/play',
    GAME_MULTIPLAYER_SERVICE_API : 'http://localhost:8080/rock-paper-scissors/play'
});

app.config(['$stateProvider', '$urlRouterProvider',
    function($stateProvider, $urlRouterProvider) {

        $urlRouterProvider.otherwise('/');

        $stateProvider
            .state('home', {
                url: '/',
                templateUrl: 'templates/list.html',
                controller:'GameController',
                controllerAs:'ctrl'
            });

        $stateProvider
            .state('multiplayer', {
                url: '/multiplayer',
                templateUrl: 'templates/list-multiplayer.html',
                controller:'GameMultiPlayerController',
                controllerAs:'ctrl'
            });


    }]);