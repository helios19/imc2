describe('GameController', function() {

  beforeEach(module('rockPaperScissors'));

  it('should create a game model objects', inject(function($controller) {
    var scope = {};
    var ctrl = $controller('GameController', {$scope: scope});

    expect(ctrl.game).toBeDefined();
    expect(ctrl.previousGames).toBeDefined();
    expect(ctrl.previousGames.length).toBe(0);
    expect(ctrl.gameResult).toEqual('');
    expect(ctrl.submit).toBeDefined();
    expect(ctrl.reset).toBeDefined();
    expect(ctrl.errorMessage).toEqual('');
    expect(ctrl.done).toEqual(false);
    expect(ctrl.onlyIntegers).toEqual(/^\d+$/);
    expect(ctrl.onlyNumbers).toEqual(/^\d+([,.]\d+)?$/);

  }));

});