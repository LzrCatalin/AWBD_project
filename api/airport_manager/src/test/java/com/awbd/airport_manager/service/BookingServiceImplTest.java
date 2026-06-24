package com.awbd.airport_manager.service;

import com.awbd.airport_manager.config.security.UserInfo;
import com.awbd.airport_manager.dto.BookingDto;
import com.awbd.airport_manager.exception.ResourceNotFoundException;
import com.awbd.airport_manager.mapper.BookingMapper;
import com.awbd.airport_manager.model.Account;
import com.awbd.airport_manager.model.Booking;
import com.awbd.airport_manager.model.Flight;
import com.awbd.airport_manager.model.Seat;
import com.awbd.airport_manager.repository.BookingRepository;
import com.awbd.airport_manager.repository.FlightRepository;
import com.awbd.airport_manager.repository.SeatRepository;
import com.awbd.airport_manager.service.impl.AccountProvisioningService;
import com.awbd.airport_manager.service.impl.BookingServiceImpl;
import com.awbd.airport_manager.util.pagination.PagedResponse;
import com.awbd.airport_manager.util.search.dto.SearchDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private FlightRepository flightRepository;
    @Mock
    private SeatRepository seatRepository;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private AccountProvisioningService accountProvisioningService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private UUID id;
    private UUID flightId;
    private UUID seatId;
    private Booking booking;
    private BookingDto bookingDto;
    private Flight flight;
    private Account account;
    private Seat seat;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        flightId = UUID.randomUUID();
        seatId = UUID.randomUUID();

        flight = new Flight();
        flight.setId(flightId);

        account = new Account();
        account.setId(UUID.randomUUID());
        account.setEmail("user@test.com");

        seat = new Seat();
        seat.setId(seatId);

        booking = new Booking();
        booking.setId(id);
        booking.setBookingNo("ABC12345");
        booking.setBookingDate(LocalDateTime.now());
        booking.setFlight(flight);
        booking.setAccount(account);
        booking.setSeat(seat);

        bookingDto = new BookingDto();
        bookingDto.setId(id);
        bookingDto.setFlightId(flightId);
        bookingDto.setSeatId(seatId);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void setupSecurityContext(String email, String... roles) {
        List<SimpleGrantedAuthority> authorities = List.of(roles).stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
        UserInfo userInfo = new UserInfo("sub", email, "Test User", List.of(roles), authorities);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userInfo, null, authorities)
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void search_asAdmin_returnsAllBookings() {
        setupSecurityContext("admin@test.com", "ROLE_ADMIN");
        SearchDTO searchDTO = new SearchDTO();
        Page<Booking> page = new PageImpl<>(List.of(booking));

        when(bookingRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto);

        PagedResponse<BookingDto> result = bookingService.search(searchDTO);

        assertThat(result.getItems()).hasSize(1);
    }

    @Test
    @SuppressWarnings("unchecked")
    void search_asRegularUser_addsEmailFilter() {
        setupSecurityContext("user@test.com", "ROLE_USER");
        SearchDTO searchDTO = new SearchDTO();
        Page<Booking> page = new PageImpl<>(List.of(booking));

        when(bookingRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto);

        PagedResponse<BookingDto> result = bookingService.search(searchDTO);

        assertThat(searchDTO.getFilters()).anyMatch(f -> f.getField().equals("account.email"));
        assertThat(result.getItems()).hasSize(1);
    }

    @Test
    void getById_whenFound_returnsDto() {
        when(bookingRepository.findById(id)).thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto);

        BookingDto result = bookingService.getById(id);

        assertThat(result.getId()).isEqualTo(id);
    }

    @Test
    void getById_whenNotFound_throwsResourceNotFoundException() {
        when(bookingRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_savesBookingWithCorrectData() {
        setupSecurityContext("user@test.com", "ROLE_USER");

        when(flightRepository.findById(flightId)).thenReturn(Optional.of(flight));
        when(accountProvisioningService.resolveCurrentAccount()).thenReturn(account);
        when(seatRepository.findById(seatId)).thenReturn(Optional.of(seat));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto);

        BookingDto result = bookingService.create(bookingDto);

        verify(bookingRepository).save(any(Booking.class));
        verify(accountProvisioningService).resolveCurrentAccount();
        assertThat(result).isNotNull();
    }

    @Test
    void create_whenFlightNotFound_throwsResourceNotFoundException() {
        setupSecurityContext("user@test.com", "ROLE_USER");
        when(flightRepository.findById(flightId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.create(bookingDto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_whenAccountCannotBeResolved_propagatesException() {
        when(flightRepository.findById(flightId)).thenReturn(Optional.of(flight));
        when(accountProvisioningService.resolveCurrentAccount())
                .thenThrow(new IllegalStateException("no email claim"));

        assertThatThrownBy(() -> bookingService.create(bookingDto))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void create_whenSeatNotFound_throwsResourceNotFoundException() {
        when(flightRepository.findById(flightId)).thenReturn(Optional.of(flight));
        when(accountProvisioningService.resolveCurrentAccount()).thenReturn(account);
        when(seatRepository.findById(seatId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.create(bookingDto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_whenFound_deletesBooking() {
        when(bookingRepository.findById(id)).thenReturn(Optional.of(booking));

        bookingService.delete(id);

        verify(bookingRepository).delete(booking);
    }

    @Test
    void delete_whenNotFound_throwsResourceNotFoundException() {
        when(bookingRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.delete(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
