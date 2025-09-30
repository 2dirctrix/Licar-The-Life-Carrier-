using licar_wpf.Models;
using licar_wpf.Services;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Input;

namespace licar_wpf.ViewModels
{
    public class RelayCommand : ICommand
    {
        private readonly Action<object> _execute;
        private readonly Predicate<object> _canExecute;
        public event EventHandler CanExecuteChanged;
        public RelayCommand(Action<object> execute, Predicate<object> canExecute = null)
        {
            _execute = execute ?? throw new ArgumentNullException(nameof(execute));
            _canExecute = canExecute;
        }
        public bool CanExecute(object parameter) => _canExecute == null || _canExecute(parameter);
        public void Execute(object parameter) => _execute(parameter);
        public void RaiseCanExecuteChanged() => CanExecuteChanged?.Invoke(this, EventArgs.Empty);
    }
    public class LoginViewModel : ViewModelBase
    {
        private readonly AuthService _authService;
        private readonly NavigationService _navigationService;
        private readonly SseService _sseService;

        private readonly Func<NurseViewModel> _nurseViewModelFactory;
        private readonly Func<PharmacistViewModel> _pharmacistViewModelFactory;

        private int _userId;
        public int UserId { get => _userId; set { _userId = value; OnPropertyChanged(); } }

        private string _userName;
        public string UserName
        {
            get => _userName;
            set
            {
                _userName = value;
                OnPropertyChanged();
                (LoginCommand as RelayCommand)?.RaiseCanExecuteChanged();
            }
        }

        private string _errorMessage;
        public string ErrorMessage { get => _errorMessage; set { _errorMessage = value; OnPropertyChanged(); } }

        public bool IsNurseRole { get; set; } = true;

        public ICommand LoginCommand { get; }

        public LoginViewModel(AuthService authService, NavigationService navigationService, SseService sseService, Func<NurseViewModel> nurseViewModelFactory, Func<PharmacistViewModel> pharmacistViewModelFactory)
        {
            _authService = authService;
            _navigationService = navigationService;
            _sseService = sseService;
            _nurseViewModelFactory = nurseViewModelFactory;
            _pharmacistViewModelFactory = pharmacistViewModelFactory;

            LoginCommand = new RelayCommand(async (p) => await ExecuteLogin());
        }

        private async Task ExecuteLogin()
        {
            ErrorMessage = string.Empty;

            try
            {
                if (IsNurseRole)
                {
                    var nurse = await _authService.LoginNurseAsync(UserId, UserName);
                    UserSession.Login(new CurrentUser { Id = nurse.NurseId, Name = nurse.Name, Role = UserRole.Nurse, Department = nurse.Department });

                    Task.Run(() => _sseService.ConnectAsync(UserRole.Nurse, nurse.NurseId));

                    //_navigationService.NavigateTo(new NurseViewModel(_navigationService, new NurseService(new ApiClient()), _sseService));
                    _navigationService.NavigateTo(_nurseViewModelFactory());
                }
                else
                {
                    var pharmacist = await _authService.LoginPharmacistAsync(UserId, UserName);
                    UserSession.Login(new CurrentUser { Id = pharmacist.PharmacistId, Name = pharmacist.Name, Role = UserRole.Pharmacist });

                    Task.Run(() => _sseService.ConnectAsync(UserRole.Pharmacist, pharmacist.PharmacistId));

                    //_navigationService.NavigateTo(new PharmacistViewModel(_navigationService, new PharmacistService(new ApiClient()), _sseService));
                    _navigationService.NavigateTo(_pharmacistViewModelFactory());
                }
            }
            catch (Exception ex)
            {
                string actualExceptionType = ex.GetType().FullName;

                string exceptionMessage = ex.Message;

                string innerExceptionDetails = ex.InnerException?.ToString();

                string stackTrace = ex.StackTrace;

                System.Diagnostics.Debug.WriteLine($"[ERROR] Type: {actualExceptionType}");
                System.Diagnostics.Debug.WriteLine($"[ERROR] Message: {exceptionMessage}");
                System.Diagnostics.Debug.WriteLine($"[ERROR] Inner Exception: {innerExceptionDetails}");
                System.Diagnostics.Debug.WriteLine($"[ERROR] Stack Trace: {stackTrace}");


                ErrorMessage = "사번과 이름을 확인해주세요.";

                MessageBox.Show(ErrorMessage.ToString(), "실패", MessageBoxButton.OK, MessageBoxImage.Error);
            }
        }
    }
}
