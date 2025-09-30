using licar_wpf.Models;
using licar_wpf.Services;
using System;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text.Json;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Input;

namespace licar_wpf.ViewModels
{
    public class PharmacistViewModel : ViewModelBase
    {
        private readonly PharmacistService _pharmacistService;
        private readonly SseService _sseService;
        private readonly ToastNotificationService _toastService;

        public ObservableCollection<Transport> TransportList { get; } = new ObservableCollection<Transport>();
        public ObservableCollection<Prescription> PrescriptionList { get; } = new ObservableCollection<Prescription>();
        public ObservableCollection<DrugViewModel> DrugList { get; } = new ObservableCollection<DrugViewModel>();

        private bool _isBusy;
        public bool IsBusy
        {
            get => _isBusy;
            set { _isBusy = value; OnPropertyChanged(); }
        }

        private Transport _selectedTransport;
        public Transport SelectedTransport
        {
            get => _selectedTransport;
            set
            {
                if (_selectedTransport != value)
                {
                    _selectedTransport = value;
                    OnPropertyChanged();

                    LoadDetailsForSelectedTransport();
                }
            }
        }

        private Transport _transportDetails;
        public Transport TransportDetails { get; private set; }

        private Prescription _selectedPrescription;
        public Prescription SelectedPrescription
        {
            get => _selectedPrescription;
            set
            {
                if (_selectedPrescription != value)
                {
                    _selectedPrescription = value;
                    OnPropertyChanged();

                    LoadPrescriptionDetailsAsync();
                }
            }
        }

        private Prescription _prescriptionDetails;
        public Prescription PrescriptionDetails { get; private set; }

        public ICommand LoadTransportsCommand { get; }

        public PharmacistViewModel(NavigationService navigationService, PharmacistService pharmacistService, SseService sseService, ToastNotificationService toastService)
        {
            _pharmacistService = pharmacistService;
            _sseService = sseService;
            _toastService = toastService;

            LoadTransportsCommand = new RelayCommand(async (p) => await LoadTransportsAsync(), (p) => !IsBusy);

            _sseService.TransportRequested += OnTransportRequested;
            _sseService.DrugRecognized += OnDrugRecognized;
            LoadTransportsCommand.Execute(null);
        }

        public void CleanUp()
        {
            _sseService.TransportRequested -= OnTransportRequested;
            _sseService.DrugRecognized -= OnDrugRecognized;
        }

        private async Task LoadTransportsAsync()
        {
            IsBusy = true;
            try
            {
                TransportList.Clear();
                var transports = await _pharmacistService.GetTransportsAsync();
                foreach (var transport in transports)
                {
                    TransportList.Add(transport);
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show($"운반 목록을 불러오는 데 실패했습니다: {ex.Message}", "오류", MessageBoxButton.OK, MessageBoxImage.Error);
            }
            finally
            {
                IsBusy = false;
            }
        }

        private async void LoadDetailsForSelectedTransport()
        {
            PrescriptionList.Clear();

            TransportDetails = null;
            PrescriptionDetails = null;
            DrugList.Clear();

            OnPropertyChanged(nameof(TransportDetails));
            OnPropertyChanged(nameof(PrescriptionDetails));

            if (SelectedTransport == null) return;

            IsBusy = true;
            try
            {
                var details = await _pharmacistService.GetTransportDetailAsync(SelectedTransport.TransportId);
                TransportDetails = details;
                OnPropertyChanged(nameof(TransportDetails));

                if (details?.Prescriptions != null)
                {
                    foreach (var prescription in details.Prescriptions)
                    {
                        PrescriptionList.Add(prescription);
                    }
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show($"운반 상세 정보를 불러오는 데 실패했습니다: {ex.Message}", "오류", MessageBoxButton.OK, MessageBoxImage.Error);
            }
            finally
            {
                IsBusy = false;
            }
        }

        private async void LoadPrescriptionDetailsAsync()
        {
            DrugList.Clear();
            PrescriptionDetails = null;

            OnPropertyChanged(nameof(PrescriptionDetails));

            if (SelectedTransport == null || SelectedPrescription == null) return;

            IsBusy = true;
            try
            {
                var details = await _pharmacistService.GetPrescriptionDetailForTransportAsync(SelectedTransport.TransportId, SelectedPrescription.PrescriptionId);
                PrescriptionDetails = details;
                OnPropertyChanged(nameof(PrescriptionDetails));

                if (details?.Drugs != null)
                {
                    foreach (var drugModel in details.Drugs)
                    {
                        DrugList.Add(new DrugViewModel(drugModel));
                    }
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show($"처방전 상세 정보를 불러오는 데 실패했습니다: {ex.Message}", "오류", MessageBoxButton.OK, MessageBoxImage.Error);
            }
            finally
            {
                IsBusy = false;
            }
        }

        private void OnTransportRequested(string jsonData)
        {
            _toastService.Show("새로운 약 운반 요청이 도착했습니다.");

            Application.Current.Dispatcher.Invoke(() =>
            {
                if (!IsBusy)
                {
                    LoadTransportsCommand.Execute(null);
                }
            });
        }

        private void OnDrugRecognized(string jsonData)
        {
            Application.Current.Dispatcher.Invoke(() =>
            {
                try
                {
                    if (!DrugList.Any()) return;

                    var recognizedDrugUpdate = JsonSerializer.Deserialize<Drug>(jsonData, new JsonSerializerOptions { PropertyNameCaseInsensitive = true });
                    if (recognizedDrugUpdate == null) return;


                    var drugToUpdate = DrugList.FirstOrDefault(d => d.DrugId == recognizedDrugUpdate.DrugId);

                    if (drugToUpdate != null)
                    {
                        drugToUpdate.Recognized = recognizedDrugUpdate.Recognized;
                    }
                }
                catch (Exception ex)
                {
                    System.Diagnostics.Debug.WriteLine($"[SSE Drug Recognition ERROR] {ex.Message}");
                }
            });
        }
    }
}