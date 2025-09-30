using licar_wpf.Models;
using licar_wpf.Services;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Text.Json;
using System.Text.Json.Serialization;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Input;

namespace licar_wpf.ViewModels
{
    public partial class NurseViewModel : ViewModelBase
    {
        private readonly NurseService _nurseService;
        private readonly SseService _sseService;
        private readonly ToastNotificationService _toastService;

        private string _searchKeyword;
        public string SearchKeyword { get => _searchKeyword; set { _searchKeyword = value; OnPropertyChanged(); } }

        public ObservableCollection<Patient> PatientList { get; } = new ObservableCollection<Patient>();
        public ObservableCollection<Notification> NotificationList { get; } = new ObservableCollection<Notification>();
        public ObservableCollection<Prescription> ValidPrescriptionList { get; } = new ObservableCollection<Prescription>();

        private Patient _selectedPatient;
        public Patient SelectedPatient
        {
            get => _selectedPatient;
            set
            {
                _selectedPatient = value;
                OnPropertyChanged();
                LoadPatientDetails(value);
                LoadValidPrescriptionsAsync(value);

                (RequestTransportCommand as RelayCommand)?.RaiseCanExecuteChanged();
            }
        }

        private Patient _patientDetails;
        public Patient PatientDetails
        {
            get => _patientDetails;
            private set
            {
                _patientDetails = value;
                OnPropertyChanged();
            }
        }

        private Prescription _selectedPrescription;
        public Prescription SelectedPrescription
        {
            get => _selectedPrescription;
            set
            {
                _selectedPrescription = value;
                OnPropertyChanged();

                LoadPrescriptionDetailsAsync(value);
            }
        }

        private Prescription _prescriptionDetails;
        public Prescription PrescriptionDetails
        {
            get => _prescriptionDetails;
            set
            {
                _prescriptionDetails = value;
                OnPropertyChanged();
            }
        }

        public string NurseName => UserSession.CurrentUser?.Name;

        public ICommand SearchCommand { get; }
        public RelayCommand RequestTransportCommand { get; }

        public NurseViewModel(NavigationService navigationService, NurseService nurseService, SseService sseService, ToastNotificationService toastService)
        {
            _nurseService = nurseService;
            _sseService = sseService;
            _toastService = toastService;

            SearchCommand = new RelayCommand(async (p) => await SearchPatients());
            RequestTransportCommand = new RelayCommand(async (p) => await RequestTransport(), (p) => SelectedPatient != null);

            _sseService.TransportCompleted += OnTransportCompleted;
        }

        public void CleanUp()
        {
            _sseService.TransportCompleted -= OnTransportCompleted;
        }

        private async Task SearchPatients()
        {
            try
            {
                IEnumerable<Patient> patients;

                if (string.IsNullOrWhiteSpace(SearchKeyword))
                {
                    patients = await _nurseService.GetPatientsAsync();
                }
                else
                {
                    patients = await _nurseService.SearchPatientsAsync(SearchKeyword);
                }

                PatientList.Clear();

                foreach (Patient patient in patients)
                {
                    PatientList.Add(patient);
                }
            }
            catch (Exception ex)
            {
                System.Diagnostics.Debug.WriteLine($"[ Patient Search ERROR] {ex.Message}");
            }
        }

        private async void LoadPatientDetails(Patient patient)
        {
            if (patient == null)
            {
                PatientDetails = null;
                return;
            }
            var details = await _nurseService.GetPatientDetailAsync(patient.PatientId);

            PatientDetails = details;
        }

        private async void LoadValidPrescriptionsAsync(Patient patient)
        {
            ValidPrescriptionList.Clear();
            PrescriptionDetails = null;

            if (patient == null) return;

            var prescriptions = await _nurseService.GetValidPrescriptionsAsync(patient.PatientId, DateTime.Now);
            foreach (var prescription in prescriptions)
            {
                ValidPrescriptionList.Add(prescription);
            }
        }

        private async void LoadPrescriptionDetailsAsync(Prescription prescription)
        {
            if (prescription == null)
            {
                PrescriptionDetails = null;
                return;
            }

            try
            {
                var prescription_details = await _nurseService.GetPrescriptionDetailsAsync(prescription.PrescriptionId);
                PrescriptionDetails = prescription_details;
            }
            catch (Exception ex)
            {
                MessageBox.Show($"처방전 상세 정보를 불러오는 데 실패했습니다.\n{ex.Message}");
                PrescriptionDetails = null;
            }
        }

        private async Task RequestTransport()
        {
            try
            {
                var result = await _nurseService.RequestTransportAsync(1, UserSession.CurrentUser.Id, 1, PatientDetails.PatientId);

                Application.Current.Dispatcher.Invoke(() =>
                {
                    MessageBox.Show($"성공적으로 요청되었습니다. \n( 요청번호 : {result.TransportId})", "성공", MessageBoxButton.OK, MessageBoxImage.Information);
                });
            }
            catch (Exception ex)
            {
                MessageBox.Show($"요청에 실패했습니다. \n{ex}", "실패", MessageBoxButton.OK, MessageBoxImage.Error);
            }
        }

        private class TransportCompletedMessage
        {
            [JsonPropertyName("transportId")]
            public int TransportId { get; set; }

            [JsonPropertyName("successTime")]
            public DateTime SuccessTime { get; set; }

            [JsonPropertyName("nurseId")]
            public int NurseId { get; set; }

            [JsonPropertyName("pharmacistId")]
            public int PharmacistId { get; set; }
        }

        private void OnTransportCompleted(string jsonData)
        {
            var message = JsonSerializer.Deserialize<TransportCompletedMessage>(jsonData);

            _toastService.Show($"요청 번호 {message.TransportId}이 운반되었습니다.");

            Application.Current.Dispatcher.Invoke(() =>
            {
                NotificationList.Add(new Notification($"운반 완료 알림 : {jsonData}"));
            });
        }
    }
}
