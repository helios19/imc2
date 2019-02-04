'use strict';
 
angular.module('rockPaperScissors').controller('GameController',
    ['GameService', '$scope', '$window', '$modal',  function( GameService, $scope, $window, $modal) {
 
        var self = this;
        self.game = {};
        self.previousGames=[];
        self.gameResult='';

        self.submit = submit;
        self.reset = reset;

        self.errorMessage = '';
        self.done = false;
        self.gameOverMessage = "No tries left";

        self.gameCounter = 0;
        self.gameMaxTries = 5;

        self.onlyIntegers = /^\d+$/;
        self.onlyNumbers = /^\d+([,.]\d+)?$/;


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
            console.log('Submitting : ' + self.game.playerSymbol + '|' + self.game.uuid);

            // if number of max tries reached then reset game
            if (self.gameCounter >= self.gameMaxTries) {

                // show modal popup
                showModal(self.gameOverMessage)

            } else {

            // increment game counter
            self.gameCounter = self.gameCounter + 1;

                GameService.playRps(self.game)
                    .then(
                        function (response) {
                            console.log('Games loaded successfully');
                            console.log('uuid : ' + response.data.uuid);
                            console.log('player : ' + response.data.player);
                            console.log('computer : ' + response.data.computer);
                            console.log('result : ' + response.data.result);
                            self.errorMessage='';
                            self.done = true;
                            self.previousGames = response.data.history;
                            self.game.playerSymbol = response.data.playerSymbol;
                            self.game.computerSymbol = response.data.computerSymbol;
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

    }
    ]);

