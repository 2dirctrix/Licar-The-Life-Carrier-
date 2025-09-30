using licar_wpf.Services;
using licar_wpf.ViewModels;
using System.Configuration;
using System.Data;
using System.Windows;

namespace licar_wpf
{
    /// <summary>
    /// Interaction logic for App.xaml
    /// </summary>
    public partial class App : Application
    {
        protected override void OnStartup(StartupEventArgs e)
        {
            base.OnStartup(e);

            //string apiBaseUrl = "https://j13c206.p.ssafy.io";
            string apiBaseUrl = "http://localhost";

            var apiClient = new ApiClient(apiBaseUrl);
            var navigationService = new NavigationService();
            var sseService = new SseService(apiBaseUrl);
            var toastNotifiactionService = new ToastNotificationService();

            var authService = new AuthService(apiClient);
            var nurseService = new NurseService(apiClient);
            var pharmacistService = new PharmacistService(apiClient);

            Func<NurseViewModel> nurseViewModelFactory = () => new NurseViewModel(navigationService, nurseService, sseService, toastNotifiactionService);
            Func<PharmacistViewModel> pharmacistViewModelFactory = () => new PharmacistViewModel(navigationService, pharmacistService, sseService, toastNotifiactionService);

            Func<LoginViewModel> loginViewModelFactory = () => new LoginViewModel(authService, navigationService, sseService, nurseViewModelFactory, pharmacistViewModelFactory);

            var mainViewModel = new MainViewModel(navigationService, sseService, toastNotifiactionService, loginViewModelFactory);

            var mainWindow = new MainWindow
            {
                DataContext = mainViewModel
            };

            mainWindow.Show();
        }
    }

}
