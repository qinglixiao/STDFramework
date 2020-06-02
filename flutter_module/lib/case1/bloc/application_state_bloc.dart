import 'package:fluttermodule/case1/bloc/application_model.dart';
import 'package:fluttermodule/case1/model/authentication_state.dart';
import 'package:fluttermodule/common/bloc_provider.dart';
import 'package:rxdart/rxdart.dart';

class ApplicationStateBloc implements BlocBase {
  ///
  /// Application State
  ///
  ApplicationModel applicationState = ApplicationModel();

  BehaviorSubject<ApplicationModel> _applicationStateController =
      BehaviorSubject<ApplicationModel>.seeded(ApplicationModel());

  Stream<ApplicationModel> get applicationModel => _applicationStateController;

  @override
  void dispose() {
    _applicationStateController?.close();
  }

  ///
  /// Used to save the authentication data
  ///
  void logout() {
    applicationState.firstName = "";
    applicationState.lastName = "";
    applicationState.authenticationState = AuthenticationState.notAuthenticated;
    _applicationStateController.sink.add(applicationState);
  }

  ///
  /// Used to save the authentication data
  ///
  void login({String firstName, String lastName}) {
    applicationState.firstName = firstName;
    applicationState.lastName = lastName;
    applicationState.authenticationState = AuthenticationState.authenticated;
    applicationState.isWorking = false;
    _applicationStateController.sink.add(applicationState);
  }

  ///
  /// Used to simulate an authentication
  ///
  void doAuthenticate() async {
    //
    // Notify that we are performing the authentication
    //
    applicationState.isWorking = true;
    _applicationStateController.sink.add(applicationState);

    //
    // Simulate delayed authentication
    //
    Future.delayed(Duration(seconds: 2)).then((_) {
      login(
        firstName: "John",
        lastName: "Doe",
      );
    });
  }
}
