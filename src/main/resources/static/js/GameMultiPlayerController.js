'use strict';
 
angular.module('rockPaperScissors').controller('GameMultiPlayerController',
    ['GameMultiPlayerService', '$scope', '$window', '$modal',  function( GameMultiPlayerService, $scope, $window, $modal) {
 
        var self = this;
        self.game = {};
        self.game.playerSymbols = [];
        self.previousGames=[];
        self.gameResult='';

        self.submit = submit;
        self.reset = reset;
        self.addNewPlayer= addNewPlayer;

        self.errorMessage = '';
        self.done = false;
        self.gameOverMessage = "No tries left";

        self.gameCounter = 0;
        self.gameMaxTries = 5;

        self.onlyIntegers = /^\d+$/;
        self.onlyNumbers = /^\d+([,.]\d+)?$/;

        self.templatePlayers = [];


        var ModalInstanceCtrl = function($scope, $modalInstance, $modal, item) {

             $scope.item = item;

              $scope.ok = function () {
                $modalInstance.close();
              };

              $scope.cancel = function () {
                $modalInstance.dismiss('cancel');
              };

              $scope.restart = function () {
                reset();
                $modalInstance.close();
              }
        };
 
        function submit() {
            console.log('Submitting : ' + self.game.playerSymbols + '|' + self.game.uuid);

            // if number of max tries reached then reset game
            if (self.gameCounter >= self.gameMaxTries) {

                // show modal popup
                showModal(self.gameOverMessage)

            } else {

            // increment game counter
            self.gameCounter = self.gameCounter + 1;

                GameMultiPlayerService.playRps(self.game)
                    .then(
                        function (response) {
                            console.log('Games loaded successfully');
                            console.log('uuid : ' + response.data.uuid);
                            console.log('playerSymbols : ' + response.data.playerSymbols);
                            console.log('result : ' + response.data.result);
                            self.errorMessage='';
                            self.done = true;
                            self.previousGames = response.data.history;
                            self.game.playerSymbols = response.data.playerSymbols;
                            self.game.uuid = response.data.uuid;
                            self.gameResult = response.data.result;
                            $scope.myForm.$setPristine();
                        },
                        function (errResponse) {
                            console.error('Error while loading Games');
                            self.errorMessage = 'Error while loading Games: ' + errResponse.data.message;
                        }
                    );

                }
        }

        function reset(){
            self.errorMessage='';
            self.game={};
            self.previousGames=[];
            self.game.playerSymbol='';
            self.game.computerSymbol='';
            self.gameResult='';
            self.gameCounter = 0;
            self.templatePlayers=[];
            self.game.playerSymbols=[]
            $scope.myForm.$setPristine(); //reset Form
        }

        function showModal(modalMessage) {


            $scope.opts = {
            backdrop: true,
            backdropClick: true,
            dialogFade: false,
            keyboard: true,
            templateUrl : 'templates/modalContent.html',
            controller : ModalInstanceCtrl,
            resolve: {}
              };


            $scope.opts.resolve.item = function() {
                return angular.copy({message:modalMessage}); // pass message to Dialog
            }

            var modalInstance = $modal.open($scope.opts);

            modalInstance.result.then(function(){
                  reset();
                  //on ok button press
                },function(){
                  reset();
                  //on cancel button press
                  console.log("Modal Closed");
            });
        }

        function addNewPlayer() {
            self.templatePlayers.push('templates/row.html');
        }

    }
    ]);

